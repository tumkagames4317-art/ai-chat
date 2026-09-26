package com.ai.frankenstein.core.terminal

import android.util.Log
import com.ai.frankenstein.core.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader

class TerminalSession {
    
    companion object {
        private const val TAG = "TerminalSession"
        
        // Safe commands (no confirmation needed)
        val SAFE_COMMANDS = listOf(
            "ls", "ll", "la",
            "cd", "pwd",
            "cat", "less", "more", "head", "tail",
            "echo", "date", "time",
            "whoami", "id",
            "uname", "hostname",
            "df", "du", "free",
            "ps", "top", "htop",
            "git status", "git log", "git diff",
            "python", "python3", "pip",
            "node", "npm", "yarn"
        )
        
        // Dangerous commands (require confirmation)
        val DANGEROUS_COMMANDS = listOf(
            "rm", "mv", "cp", "dd", "mkfs",
            "chmod", "chown", "sudo", "su",
            "kill", "killall", "pkill",
            "shutdown", "reboot", "halt",
            "format", "fdisk", "parted",
            ":(){ :|:& };:",
            "wget", "curl",
            "ssh", "scp", "rsync"
        )
    }
    
    private val _outputFlow = MutableSharedFlow<String>(replay = 100)
    val outputFlow: Flow<String> = _outputFlow.asSharedFlow()
    
    private var process: Process? = null
    private var isRunning = false
    private var currentCommand: String = ""
    
    // Execute a command
    suspend fun execute(command: String, workingDirectory: String? = null): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            currentCommand = command
            isRunning = true
            
            // Emit the command to output
            _outputFlow.emit("\$ $command\n")
            
            // Check if command is safe
            val isSafe = isCommandSafe(command)
            
            if (!isSafe) {
                _outputFlow.emit("WARNING: This command may be dangerous.\n")
                _outputFlow.emit("Type 'yes' to confirm or 'no' to cancel: ")
                // In a real implementation, you'd wait for user confirmation
                _outputFlow.emit("Command cancelled for safety.\n")
                isRunning = false
                return@withContext Result.failure(Exception("Command requires confirmation"))
            }
            
            // Build the command
            val parts = command.split(" ".toRegex(), limit = 2)
            val cmd = parts[0]
            val args = if (parts.size > 1) listOf("/system/bin/sh", "-c", command) else listOf(cmd)
            
            val processBuilder = ProcessBuilder(args)
            workingDirectory?.let { dir ->
                processBuilder.directory(java.io.File(dir))
            }
            
            processBuilder.redirectErrorStream(true)
            process = processBuilder.start()
            
            // Read output
            val reader = BufferedReader(InputStreamReader(process!!.inputStream))
            var line: String?
            
            while (reader.readLine().also { line = it } != null) {
                _outputFlow.emit(line + "\n")
            }
            
            // Wait for process to finish
            val exitCode = process!!.waitFor()
            _outputFlow.emit("\n[Process exited with code $exitCode]\n")
            
            isRunning = false
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to execute command: $command", e)
            _outputFlow.emit("Error: ${e.message}\n")
            isRunning = false
            Result.failure(e)
        }
    }
    
    // Execute Python code
    suspend fun executePython(code: String, workingDirectory: String? = null): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            currentCommand = "python3 -c \"$code\""
            isRunning = true
            
            _outputFlow.emit("\$ python3 -c \"...\"\n")
            
            val processBuilder = ProcessBuilder("python3", "-c", code)
            workingDirectory?.let { dir ->
                processBuilder.directory(java.io.File(dir))
            }
            
            processBuilder.redirectErrorStream(true)
            process = processBuilder.start()
            
            val reader = BufferedReader(InputStreamReader(process!!.inputStream))
            var line: String?
            
            while (reader.readLine().also { line = it } != null) {
                _outputFlow.emit(line + "\n")
            }
            
            val exitCode = process!!.waitFor()
            _outputFlow.emit("\n[Python exited with code $exitCode]\n")
            
            isRunning = false
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to execute Python code", e)
            _outputFlow.emit("Error: ${e.message}\n")
            isRunning = false
            Result.failure(e)
        }
    }
    
    // Execute Bash script
    suspend fun executeScript(script: String, workingDirectory: String? = null): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            currentCommand = script
            isRunning = true
            
            _outputFlow.emit("\$ bash -c \"...\"\n")
            
            val processBuilder = ProcessBuilder("/system/bin/sh", "-c", script)
            workingDirectory?.let { dir ->
                processBuilder.directory(java.io.File(dir))
            }
            
            processBuilder.redirectErrorStream(true)
            process = processBuilder.start()
            
            val reader = BufferedReader(InputStreamReader(process!!.inputStream))
            var line: String?
            
            while (reader.readLine().also { line = it } != null) {
                _outputFlow.emit(line + "\n")
            }
            
            val exitCode = process!!.waitFor()
            _outputFlow.emit("\n[Script exited with code $exitCode]\n")
            
            isRunning = false
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to execute script", e)
            _outputFlow.emit("Error: ${e.message}\n")
            isRunning = false
            Result.failure(e)
        }
    }
    
    // Check if command is safe
    private fun isCommandSafe(command: String): Boolean {
        val lowerCommand = command.lowercase()
        
        // Check for dangerous patterns
        DANGEROUS_COMMANDS.forEach { dangerous ->
            if (lowerCommand.startsWith(dangerous) || 
                lowerCommand.contains(" $dangerous ") ||
                lowerCommand.contains("\t$dangerous")) {
                return false
            }
        }
        
        // Check for pipe to dangerous commands
        if (lowerCommand.contains("|") || lowerCommand.contains(";") || lowerCommand.contains("&")) {
            return false
        }
        
        // Check for root operations
        if (lowerCommand.contains("sudo") || lowerCommand.contains("su")) {
            return false
        }
        
        // Check for file system operations
        if (lowerCommand.contains("rm -rf") || 
            lowerCommand.contains("mkfs") ||
            lowerCommand.contains("dd if=")) {
            return false
        }
        
        return true
    }
    
    // Terminate current process
    fun terminate() {
        try {
            process?.destroyForcibly()
            isRunning = false
            _outputFlow.tryEmit("\n[Process terminated]\n")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to terminate process", e)
        }
    }
    
    // Clear output
    suspend fun clearOutput() {
        _outputFlow.emit("\n[Terminal cleared]\n")
    }
    
    // Get current working directory
    suspend fun getCurrentDirectory(): Result<String> = withContext(Dispatchers.IO) {
        return@withContext try {
            val process = ProcessBuilder("pwd").start()
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val directory = reader.readLine()
            process.waitFor()
            Result.success(directory ?: "/")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // List files in directory
    suspend fun listFiles(directory: String = "."): Result<List<String>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val process = ProcessBuilder("ls", "-la", directory).start()
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val files = mutableListOf<String>()
            var line: String?
            
            while (reader.readLine().also { line = it } != null) {
                files.add(line!!)
            }
            
            process.waitFor()
            Result.success(files)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Check if session is running
    fun isSessionRunning(): Boolean = isRunning
    
    // Get current command
    fun getCurrentCommand(): String = currentCommand
}
