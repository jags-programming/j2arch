package com.pjsoft.j2arch.core.util;

/**
 * ProgressListener
 * 
 * Interface for tracking progress and status updates during long-running tasks.
 * This interface is designed to be implemented by classes that need to monitor
 * progress and display status messages, either in a CLI or GUI environment.
 * 
 * Responsibilities:
 * - Provide a method to update progress as a percentage.
 * - Provide a method to send status messages during task execution.
 * 
 * Usage Example:
 * {@code
 * ProgressListener listener = new ProgressListener() {
 *     @Override
 *     public void onProgressUpdate(double progress) {
 *         System.out.println("Progress: " + (int) (progress * 100) + "%");
 *     }
 * 
 *     @Override
 *     public void onStatusUpdate(String message) {
 *         System.out.println("Status: " + message);
 *     }
 * };
 * listener.onProgressUpdate(0.5); // 50% progress
 * listener.onStatusUpdate("Task is halfway done.");
 * }
 * 
 * Author: PJSoft
 * Since: 1.0
 */
public interface ProgressListener {
    /**
     * Called to update the progress.
     * 
     * Responsibilities:
     * - Notify the listener of the current progress as a percentage.
     * 
     * @param progress A value between 0.0 (0%) and 1.0 (100%).
     */
    void onProgressUpdate(double progress);

    /**
     * Called to send a status message.
     * 
     * Responsibilities:
     * - Notify the listener of the current status message.
     * 
     * @param message The status message to display.
     */
    void onStatusUpdate(String message);
}