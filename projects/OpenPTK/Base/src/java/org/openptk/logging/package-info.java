/**
 * Provides centralized Logging service to be used by all OpenPTK objects to log messages of different logging levels.  
 * The initial implementation will log to the levels of:
 * <p>
 * <ul>
 * <li>ERROR
 * <li>WARNING
 * <li>INFO (default)
 * </ul>
 * In general, the Java Logger utilities makes use of 7 different levels of 
 * logging and these may be used in future releases if requested.<br>
 * <p>
 * The OpenPTK Logger also includes the ability to manage several logging instances at one time.  
 * When an OpenPTK LoggingIF is created, a name can be used to refer to that instance at a later time.
 *
 * @since 2.0
 */
package org.openptk.logging;
