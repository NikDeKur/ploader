# Plugin Loader (ploader)

A utility that provides the ability to easily interact with the minecraft server. Allows you to send commands and move
files.

## Installation

Download the latest version from the [releases page](https://github.com/NikDeKur/ploader/releases)

Anywhere you like, place the jar and add config.yml, which will contain the necessary settings and commands.

## Basic Configuration

If you don't need to run commands, you may not create a `rcon` section.
If you run a command without rcon configuration, ploader will run nothing.

If you don't need to move files with SFTP, you may not create a `sftp` section, ploader will use local files.

Remember to change the `host`, `port`, `password`, `username` and `password` fields.

```yaml
rcon:
  host: "127.0.0.1"
  port: 8080
  password: "password"

sftp:
  host: "127.0.0.1"
  port: 8080
  username: "username"
  password: "password"
```

### Logging

Ploader currently uses [Logback](https://logback.qos.ch/) for logging with root logger level set to INFO.
You can set your own logback configuration
by adding `-Dlogback.configurationFile=/path/to/logback.xml` to the JVM options.

## Actions Configuration

The `actions` section contains a list of actions that will be executed in order.

### Command

Allows you to run a command on the server.
Parameters:

- `action` - action type, must be `command`
- `command` - command to run

### Remove

Allows you to delete a file at the specified destination.
Parameters:

- `action` - action type, must be `remove`
- `destination` - path to the file to delete

### Upload

Allows you to upload a file to the specified destination.
Parameters:

- `action` - action type, must be `upload`
- `source` - path to the file to upload

## Examples

### Example with 'reload confirm' command

```yaml
actions:
  - action: "upload"
    source: "*path*/niceplugin-1.0.0.jar"
    destination: "*path*/plugins/niceplugin-1.0.0.jar"

  - action: "command"
    command: "reload confirm"
```

### Example with [PlugMan](https://www.spigotmc.org/resources/plugmanx.88135/)

Should be faster than 'reload confirm', but might occur issues with some plugins.

```yaml
actions:
  - action: "command"
    command: "plugman unload NicePlugin"

  - action: "upload"
    source: "*path*/niceplugin-1.0.0.jar"
    destination: "*path*/plugins/niceplugin-1.0.0.jar"

  - action: "command"
    command: "plugman load NicePlugin"
```

## Example usage with IntelliJ IDEA

1. Create a folder in the project root directory, for example `ploader`
2. Add the jar and config.yml to the folder
3. Add a new JAR application configuration in the `Edit Configurations` window with the following settings:
    - `Path to JAR` - `*path*/ploader/ploader-1.0.0.jar`
    - `Working directory` - `*path*/ploader`
4. Add before launch task `jar` or `shadowJar` on your module to create jar before running the plugin loader
5. Run the configuration and see automatic plugin deployment