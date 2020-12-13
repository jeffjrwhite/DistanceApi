package com.none2clever.dapi

import ch.qos.logback.classic.{Level, Logger}
import com.typesafe.config.{Config, ConfigException, ConfigFactory, ConfigRenderOptions}
import scala.util.{Failure, Success, Try}

object AppConfig {

  val logRoot: Logger = org.slf4j.LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME).asInstanceOf[Logger]
  val currentLogLevel = logRoot.getLevel

  private def printEnvironment = {
    import scala.collection.JavaConversions._
    val environmentVars = System.getenv()
    logRoot.debug("System.getenv", currentLogLevel)
    for ((k, v) <- environmentVars)
      logRoot.debug(s"key: $k, value: $v", currentLogLevel)

    logRoot.debug("System.getProperties", currentLogLevel)
    for ((k, v) <- properties)
      logRoot.debug(s"key: $k, value: $v", currentLogLevel)
  }

  /**
    * Function to load the default application.conf and then load any included configuration files or direct to
    * a HTTP URL data source or a MongoDB data source
    */
  lazy val myConfig = {
    var config = ConfigFactory.load() // Load first application.conf found in classpath
    val showconfig = (config.hasPath("show.app.config") && config.getString("show.app.config").equalsIgnoreCase("true"))
    val showsystem = (config.hasPath("show.system.properties") && config.getString("show.system.properties").equalsIgnoreCase("true"))
    // Show system environment and the load configuration properties?
    if (showsystem)
      printEnvironment
    if (showconfig)
      logRoot.debug(config.toString, currentLogLevel)
    // Return this configuration object
    config
  }

  /**
    * Overriding environment name - this is used as prefix to key values first when resolving keys
    */
   val myEnvironment = {
    val environment = Try {
      myConfig.getString("environment")
    } match {
      case Success(value) =>
        value
      case Failure(ex) =>
        "default" // No special environment specified - set as default
    }
     logRoot.debug(s"Environment [$environment] loaded", currentLogLevel)
    environment
  }

  //Constants for different sources
  private val systemSource = "System Configuration"
  private val appSource = "Application Configuration"
  private val defaultSource = "Default Value"

  //System properties
  lazy val properties = System.getProperties

  /**
    * Function to get a named configuration property from a loaded configuration or if not found throw an exception
    *
    * @param confKey key name
    * @param config  loaded configuration (override)
    * @param environment
    * @return
    */
  def getConfigOrElseFail(confKey: String, config: Config = myConfig,
                          environment: String = myEnvironment): String =
    Try {
      // Try to get key with overriding environment prefix
      if (config.hasPath(s"${environment}.$confKey"))
        config.getString(s"${environment}.$confKey")
      else
      // Try as a default (without environment prefix)
        config.getString(confKey)
    } match {
      case Success(v) =>
        v
      case Failure(ex) =>
        throw ex
    }

  /**
    * Function to get a named configuration property from a loaded configuration or if not found provide an alternative default value
    *
    * @param confKey     key name
    * @param alternative Alternative value if key not found in configuration
    * @param config      loaded configuration (override)
    * @param environment
    * @return
    */
  def getConfigOrElseDefault(confKey: String, alternative: String,
                             config: Config = myConfig, environment: String = myEnvironment): String = {
    // Try to get key with overriding environment prefix
    if (config.hasPath(s"${environment}.$confKey"))
      config.getString(s"${environment}.$confKey")
    else
    // Try as a default (without environment prefix)
    if (config.hasPath(s"$confKey"))
      config.getString(confKey)
    else
    // Use supplied default as alternative
      alternative
  }

  def getConfigJsonOrElseDefault(confKey: String, alternative: String,
                                 config: Config = myConfig, environment: String = myEnvironment): String = {
    try {
      // Try to get key with overriding environment prefix
      if (config.hasPath(s"${environment}.$confKey"))
        config.getObject(s"${environment}.$confKey").render(ConfigRenderOptions.concise())
      else
      // Try as a default (without environment prefix)
      if (config.hasPath(s"$confKey"))
        config.getObject(confKey).render(ConfigRenderOptions.concise())
      else
      // Use supplied default as alternative
        alternative
    } catch {
      case ex: ConfigException => getConfigOrElseDefault(confKey, alternative, config, environment)
    }
  }

  /**
    * Function to get a named configuration property from a loaded configuration
    *
    * @param name   key name
    * @param config loaded configuration (override)
    * @param environment
    * @return
    */
  def getConfigOrElseFalse(name: String, config: Config = myConfig,
                           environment: String = myEnvironment): Boolean = {
    getConfigOrElseDefault(name, "false", config, environment).equalsIgnoreCase("true")
  }

  /**
    * Function to get a named system configuration property from a loaded configuration
    *
    * @param confKey key name
    * @param environment
    * @return
    */
  def getSystemConfig(confKey: String, environment: String = myEnvironment): Option[String] = {
    Option(properties.getProperty(s"${environment}.$confKey")) match {
      case Some(p) =>
        Some(p)
      case None =>
        Option(properties.getProperty(confKey))
    }
  }

  /**
    * Function to get the source for a named configuration
    * As in getConfig methods, properties with environment prefix will be taken into account before other properties
    *
    * @param confKey key name
    * @param environment
    * @return
    */
  def getConfigSource(confKey: String, config: Config = myConfig,
                      environment: String = myEnvironment): String = {
    getConfigSourceWithEnvironment(confKey, config, environment)
      .getOrElse(getSystemConfig(confKey, environment).map(_ => systemSource)
        .getOrElse(Try(getConfigOrElseFail(confKey, config, environment)).map(_ => appSource)
          .getOrElse(defaultSource)))
  }

  /**
    * Function to get the source for a named configuration
    *
    * @param confKey key name
    * @param config  loaded configuration (override)
    * @param environment
    * @return
    */
  private def getConfigSourceWithEnvironment(confKey: String, config: Config = myConfig,
                                             environment: String = myEnvironment): Option[String] = {
    Option(properties.getProperty(s"${environment}.$confKey")) match {
      case Some(_) =>
        Some(systemSource)
      case None =>
        Try(config.getString(s"${environment}.$confKey")) match {
          case Success(_) =>
            Some(appSource)
          case Failure(_) =>
            return None
        }
    }
  }

}
