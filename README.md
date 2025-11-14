<div id="top">

<!-- HEADER STYLE: CLASSIC -->
<div align="center">

<img src="KGates.png" width="30%" style="position: relative; top: 0; right: 0;" alt="Project Logo"/>

# KGATES

<em>Transforming Worlds with Seamless, Dynamic Teleportation</em>

<!-- BADGES -->
<img src="https://img.shields.io/github/license/ErikK81/KGates?style=flat&logo=opensourceinitiative&logoColor=white&color=0080ff" alt="license">
<img src="https://img.shields.io/github/last-commit/ErikK81/KGates?style=flat&logo=git&logoColor=white&color=0080ff" alt="last-commit">
<img src="https://img.shields.io/github/languages/top/ErikK81/KGates?style=flat&color=0080ff" alt="repo-top-language">
<img src="https://img.shields.io/github/languages/count/ErikK81/KGates?style=flat&color=0080ff" alt="repo-language-count">

<em>Built with the tools and technologies:</em>

<img src="https://img.shields.io/badge/Markdown-000000.svg?style=flat&logo=Markdown&logoColor=white" alt="Markdown">
<img src="https://img.shields.io/badge/XML-005FAD.svg?style=flat&logo=XML&logoColor=white" alt="XML">

</div>
<br>

---

## Table of Contents

- [Overview](#overview)
- [Getting Started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [Installation](#installation)
    - [Usage](#usage)
    - [Testing](#testing)
- [Features](#features)
- [License](#license)
- [Acknowledgment](#acknowledgment)

---

## Overview

KGates is a versatile Minecraft plugin designed to create and manage teleportation gates with advanced customization and conditional access. It empowers server administrators and developers to craft dynamic portals that respond to in-game states, permissions, and player-specific conditions.

**Why KGates?**

This project streamlines the development of complex, condition-based teleportation mechanics within Minecraft servers. The core features include:

- 🎮 **User-Friendly GUI:** Intuitive interfaces for creating and editing gates, making configuration accessible even for non-programmers.
- ⚙️ **Conditional Access:** Supports permissions, health, weather, and time-based conditions to control gate activation.
- 🔧 **Flexible Customization:** Easily define visual, auditory, and functional properties for each portal.
- 🚀 **Seamless Integration:** Built on Spigot API with support for PlaceholderAPI, ensuring compatibility and extensibility.
- 🔄 **Dynamic Management:** Create, link, and manage gates programmatically or via commands, enabling complex teleportation workflows.

---

## Features

|      | Component       | Details                                                                                     |
| :--- | :-------------- | :------------------------------------------------------------------------------------------ |
| ⚙️  | **Architecture**  | <ul><li>Modular plugin architecture based on Bukkit/Spigot API</li><li>Event-driven design for game interactions</li></ul> |
| 🔩 | **Code Quality**  | <ul><li>Clean Java code adhering to standard conventions</li><li>Uses Maven for dependency management</li></ul> |
| 📄 | **Documentation** | <ul><li>Basic README with setup instructions</li><li>Plugin.yml documentation for configuration</li></ul> |
| 🔌 | **Integrations**  | <ul><li>Spigot API for Minecraft server interactions</li><li>PlaceholderAPI for dynamic placeholders</li></ul> |
| 🧩 | **Modularity**    | <ul><li>Separate modules for core logic, commands, and event handling</li><li>Extensible via plugin.yml and API hooks</li></ul> |
| 🧪 | **Testing**       | <ul><li>Limited unit testing; primarily manual testing within server environment</li></ul> |
| ⚡️  | **Performance**   | <ul><li>Efficient event handling with minimal overhead</li><li>Uses caching where appropriate</li></ul> |
| 🛡️ | **Security**      | <ul><li>Input validation for commands and placeholders</li><li>Minimal external dependencies reduce attack surface</li></ul> |
| 📦 | **Dependencies**  | <ul><li>Managed via Maven (`pom.xml`)</li><li>Key dependencies: spigot-api, placeholderapi</li></ul> |

---

### Project Index

<details open>
	<summary><b><code>KGATES/</code></b></summary>
	<!-- __root__ Submodule -->
	<details>
		<summary><b>__root__</b></summary>
		<blockquote>
			<div class='directory-path' style='padding: 8px 0; color: #666;'>
				<code><b>⦿ __root__</b></code>
			<table style='width: 100%; border-collapse: collapse;'>
			<thead>
				<tr style='background-color: #f8f9fa;'>
					<th style='width: 30%; text-align: left; padding: 8px;'>File Name</th>
					<th style='text-align: left; padding: 8px;'>Summary</th>
				</tr>
			</thead>
				<tr style='border-bottom: 1px solid #eee;'>
					<td style='padding: 8px;'><b><a href='https://github.com/ErikK81/KGates/blob/master/README.md'>README.md</a></b></td>
					<td style='padding: 8px;'>- Defines and manages teleportation gates within the Minecraft server, enabling creation, customization, and conditional access based on permissions, health, time, and weather<br>- Facilitates user-friendly configuration via GUI and commands, ensuring controlled and flexible teleportation experiences<br>- Serves as a core component for implementing dynamic, condition-based teleportation mechanics in the overall plugin architecture.</td>
				</tr>
				<tr style='border-bottom: 1px solid #eee;'>
					<td style='padding: 8px;'><b><a href='https://github.com/ErikK81/KGates/blob/master/pom.xml'>pom.xml</a></b></td>
					<td style='padding: 8px;'>- Defines project dependencies, build configurations, and plugin integrations for a Java-based Minecraft plugin utilizing Spigot API and PlaceholderAPI<br>- Facilitates streamlined compilation, packaging, and dependency management to support the development and deployment of server-side modifications within the overall architecture<br>- Ensures compatibility with specified Minecraft versions and external plugin ecosystems.</td>
				</tr>
			</table>
		</blockquote>
	</details>
	<!-- src Submodule -->
	<details>
		<summary><b>src</b></summary>
		<blockquote>
			<div class='directory-path' style='padding: 8px 0; color: #666;'>
				<code><b>⦿ src</b></code>
			<!-- main Submodule -->
			<details>
				<summary><b>main</b></summary>
				<blockquote>
					<div class='directory-path' style='padding: 8px 0; color: #666;'>
						<code><b>⦿ src.main</b></code>
					<!-- resources Submodule -->
					<details>
						<summary><b>resources</b></summary>
						<blockquote>
							<div class='directory-path' style='padding: 8px 0; color: #666;'>
								<code><b>⦿ src.main.resources</b></code>
							<table style='width: 100%; border-collapse: collapse;'>
							<thead>
								<tr style='background-color: #f8f9fa;'>
									<th style='width: 30%; text-align: left; padding: 8px;'>File Name</th>
									<th style='text-align: left; padding: 8px;'>Summary</th>
								</tr>
							</thead>
								<tr style='border-bottom: 1px solid #eee;'>
									<td style='padding: 8px;'><b><a href='https://github.com/ErikK81/KGates/blob/master/src/main/resources/plugin.yml'>plugin.yml</a></b></td>
									<td style='padding: 8px;'>- Defines the plugin configuration for KGates, establishing its identity, version, main class, API compatibility, and author details<br>- It specifies the command for managing portals and sets administrative permissions, integrating KGates into the server architecture to enable portal management functionalities within the broader plugin ecosystem.</td>
								</tr>
							</table>
						</blockquote>
					</details>
					<!-- java Submodule -->
					<details>
						<summary><b>java</b></summary>
						<blockquote>
							<div class='directory-path' style='padding: 8px 0; color: #666;'>
								<code><b>⦿ src.main.java</b></code>
							<!-- me Submodule -->
							<details>
								<summary><b>me</b></summary>
								<blockquote>
									<div class='directory-path' style='padding: 8px 0; color: #666;'>
										<code><b>⦿ src.main.java.me</b></code>
									<!-- erik Submodule -->
									<details>
										<summary><b>erik</b></summary>
										<blockquote>
											<div class='directory-path' style='padding: 8px 0; color: #666;'>
												<code><b>⦿ src.main.java.me.erik</b></code>
											<!-- kgates Submodule -->
											<details>
												<summary><b>kgates</b></summary>
												<blockquote>
													<div class='directory-path' style='padding: 8px 0; color: #666;'>
														<code><b>⦿ src.main.java.me.erik.kgates</b></code>
													<table style='width: 100%; border-collapse: collapse;'>
													<thead>
														<tr style='background-color: #f8f9fa;'>
															<th style='width: 30%; text-align: left; padding: 8px;'>File Name</th>
															<th style='text-align: left; padding: 8px;'>Summary</th>
														</tr>
													</thead>
														<tr style='border-bottom: 1px solid #eee;'>
															<td style='padding: 8px;'><b><a href='https://github.com/ErikK81/KGates/blob/master/src/main/java/me/erik/kgates/KGates.java'>KGates.java</a></b></td>
															<td style='padding: 8px;'>- Provides the core plugin initialization and lifecycle management for KGates, orchestrating gate operations, command registration, and event handling within the Minecraft server<br>- It integrates gate management, building interfaces, and custom event listeners to enable dynamic gate creation, configuration, and interaction, forming the central hub that connects various components and ensures seamless functionality of the plugins gate-related features.</td>
														</tr>
														<tr style='border-bottom: 1px solid #eee;'>
															<td style='padding: 8px;'><b><a href='https://github.com/ErikK81/KGates/blob/master/src/main/java/me/erik/kgates/Commands.java'>Commands.java</a></b></td>
															<td style='padding: 8px;'>- Provides command handling for managing in-game gates, enabling players with appropriate permissions to create, remove, edit, browse, and teleport to gates<br>- Integrates user interface interactions and gate data management, facilitating seamless gate configuration and navigation within the server environment, and ensuring smooth user experience through command validation and tab completion support.</td>
														</tr>
													</table>
													<!-- conditions Submodule -->
													<details>
														<summary><b>conditions</b></summary>
														<blockquote>
															<div class='directory-path' style='padding: 8px 0; color: #666;'>
																<code><b>⦿ src.main.java.me.erik.kgates.conditions</b></code>
															<table style='width: 100%; border-collapse: collapse;'>
															<thead>
																<tr style='background-color: #f8f9fa;'>
																	<th style='width: 30%; text-align: left; padding: 8px;'>File Name</th>
																	<th style='text-align: left; padding: 8px;'>Summary</th>
																</tr>
															</thead>
																<tr style='border-bottom: 1px solid #eee;'>
																	<td style='padding: 8px;'><b><a href='https://github.com/ErikK81/KGates/blob/master/src/main/java/me/erik/kgates/conditions/ConditionGUI.java'>ConditionGUI.java</a></b></td>
																	<td style='padding: 8px;'>- Provides a graphical interface for managing gate conditions within the plugin, enabling users to view, add, or remove conditions associated with gate configurations<br>- Integrates with the broader gate-building system to facilitate intuitive condition editing, enhancing user interaction and customization in the plugins architecture.</td>
																</tr>
																<tr style='border-bottom: 1px solid #eee;'>
																	<td style='padding: 8px;'><b><a href='https://github.com/ErikK81/KGates/blob/master/src/main/java/me/erik/kgates/conditions/ConditionChatListener.java'>ConditionChatListener.java</a></b></td>
																	<td style='padding: 8px;'>- Handles player chat events to facilitate the input of gate conditions within the plugin<br>- It intercepts chat messages during condition setup, processes valid condition expressions, and manages cancellation commands, ensuring smooth user interaction for configuring gate behaviors<br>- This component integrates user input with the broader gate management system, enabling dynamic and user-friendly condition configuration.</td>
																</tr>
																<tr style='border-bottom: 1px solid #eee;'>
																	<td style='padding: 8px;'><b><a href='https://github.com/ErikK81/KGates/blob/master/src/main/java/me/erik/kgates/conditions/SimpleGateCondition.java'>SimpleGateCondition.java</a></b></td>
																	<td style='padding: 8px;'>- Defines a simple, flexible condition evaluation mechanism within the plugin architecture, enabling dynamic gate activation based on player-specific expressions and placeholders<br>- Facilitates conditional logic for controlling access or triggering events, integrating placeholder parsing and basic expression evaluation to support customizable, in-game gating rules aligned with the overall plugin functionality.</td>
																</tr>
															</table>
														</blockquote>
													</details>
													<!-- builder Submodule -->
													<details>
														<summary><b>builder</b></summary>
														<blockquote>
															<div class='directory-path' style='padding: 8px 0; color: #666;'>
																<code><b>⦿ src.main.java.me.erik.kgates.builder</b></code>
															<table style='width: 100%; border-collapse: collapse;'>
															<thead>
																<tr style='background-color: #f8f9fa;'>
																	<th style='width: 30%; text-align: left; padding: 8px;'>File Name</th>
																	<th style='text-align: left; padding: 8px;'>Summary</th>
																</tr>
															</thead>
																<tr style='border-bottom: 1px solid #eee;'>
																	<td style='padding: 8px;'><b><a href='https://github.com/ErikK81/KGates/blob/master/src/main/java/me/erik/kgates/builder/GateBuilderData.java'>GateBuilderData.java</a></b></td>
																	<td style='padding: 8px;'>- Defines a comprehensive data structure for configuring and managing custom gate entities within the game environment<br>- Facilitates setting visual, auditory, and functional properties, along with conditions and commands, enabling flexible gate creation and editing workflows<br>- Serves as a central component in the architecture for building, customizing, and validating gate instances in the plugin.</td>
																</tr>
																<tr style='border-bottom: 1px solid #eee;'>
																	<td style='padding: 8px;'><b><a href='https://github.com/ErikK81/KGates/blob/master/src/main/java/me/erik/kgates/builder/BuilderGUI.java'>BuilderGUI.java</a></b></td>
																	<td style='padding: 8px;'>- Provides an interactive GUI system for creating, editing, and managing portals within a Minecraft plugin<br>- Facilitates player-driven configuration of portal properties, commands, and visual effects through inventory-based interfaces and event handling<br>- Integrates with core gate management components to streamline portal setup, ensuring a user-friendly experience for building complex teleportation gateways.</td>
																</tr>
																<tr style='border-bottom: 1px solid #eee;'>
																	<td style='padding: 8px;'><b><a href='https://github.com/ErikK81/KGates/blob/master/src/main/java/me/erik/kgates/builder/GateBuilderManager.java'>GateBuilderManager.java</a></b></td>
																	<td style='padding: 8px;'>- Manages the lifecycle and state of gate-building sessions within the application, coordinating user interactions and editing locks<br>- Facilitates initiation, termination, and tracking of active gate constructions, ensuring smooth user workflows and preventing concurrent modifications<br>- Serves as a central component in the architecture that supports dynamic gate creation and editing processes.</td>
																</tr>
																<tr style='border-bottom: 1px solid #eee;'>
																	<td style='padding: 8px;'><b><a href='https://github.com/ErikK81/KGates/blob/master/src/main/java/me/erik/kgates/builder/BuilderGUIItems.java'>BuilderGUIItems.java</a></b></td>
																	<td style='padding: 8px;'>- Provides a collection of utility methods to generate customized GUI items for a gate-building interface within a Minecraft plugin<br>- These items facilitate user interactions by representing gate properties, commands, conditions, and control options, enabling intuitive configuration and management of portal gates in the overall architecture.</td>
																</tr>
																<tr style='border-bottom: 1px solid #eee;'>
																	<td style='padding: 8px;'><b><a href='https://github.com/ErikK81/KGates/blob/master/src/main/java/me/erik/kgates/builder/BuilderInputHandler.java'>BuilderInputHandler.java</a></b></td>
																	<td style='padding: 8px;'>- Facilitates user input handling within the gate-building process, enabling players to specify parameters such as radius, cooldown, particles, sounds, commands, and conditions<br>- Acts as a central controller for interpreting chat messages during gate configuration, ensuring seamless customization and validation of gate properties, thereby integrating user interactions into the overall architecture of the gate management system.</td>
																</tr>
																<tr style='border-bottom: 1px solid #eee;'>
																	<td style='padding: 8px;'><b><a href='https://github.com/ErikK81/KGates/blob/master/src/main/java/me/erik/kgates/builder/BuilderGUIListener.java'>BuilderGUIListener.java</a></b></td>
																	<td style='padding: 8px;'>- Handles player interactions within the gate-building interface, managing GUI navigation, condition configuration, and chat-based input for custom gate conditions<br>- Facilitates seamless user experience by coordinating GUI events and chat inputs, ensuring accurate gate setup and condition management within the overall plugin architecture<br>- This component is essential for user-driven gate customization and interaction flow.</td>
																</tr>
															</table>
														</blockquote>
													</details>
													<!-- manager Submodule -->
													<details>
														<summary><b>manager</b></summary>
														<blockquote>
															<div class='directory-path' style='padding: 8px 0; color: #666;'>
																<code><b>⦿ src.main.java.me.erik.kgates.manager</b></code>
															<table style='width: 100%; border-collapse: collapse;'>
															<thead>
																<tr style='background-color: #f8f9fa;'>
																	<th style='width: 30%; text-align: left; padding: 8px;'>File Name</th>
																	<th style='text-align: left; padding: 8px;'>Summary</th>
																</tr>
															</thead>
																<tr style='border-bottom: 1px solid #eee;'>
																	<td style='padding: 8px;'><b><a href='https://github.com/ErikK81/KGates/blob/master/src/main/java/me/erik/kgates/manager/GateData.java'>GateData.java</a></b></td>
																	<td style='padding: 8px;'>- Defines and manages data structures for in-game gates, encapsulating their properties, conditions, and visual/audio effects<br>- Facilitates serialization and deserialization for configuration persistence, supporting different portal types and customizable ambient and activation effects<br>- Integrates with the broader architecture to enable dynamic, configurable teleportation portals within the game environment.</td>
																</tr>
																<tr style='border-bottom: 1px solid #eee;'>
																	<td style='padding: 8px;'><b><a href='https://github.com/ErikK81/KGates/blob/master/src/main/java/me/erik/kgates/manager/GateManager.java'>GateManager.java</a></b></td>
																	<td style='padding: 8px;'>- Manages the lifecycle and configuration of teleportation gates within the plugin, including creation, retrieval, removal, and persistence<br>- Facilitates linking gates for bidirectional travel and ensures gate data is loaded from and saved to a YAML configuration file, supporting dynamic gate management and seamless integration into the overall plugin architecture.</td>
																</tr>
															</table>
														</blockquote>
													</details>
													<!-- listeners Submodule -->
													<details>
														<summary><b>listeners</b></summary>
														<blockquote>
															<div class='directory-path' style='padding: 8px 0; color: #666;'>
																<code><b>⦿ src.main.java.me.erik.kgates.listeners</b></code>
															<table style='width: 100%; border-collapse: collapse;'>
															<thead>
																<tr style='background-color: #f8f9fa;'>
																	<th style='width: 30%; text-align: left; padding: 8px;'>File Name</th>
																	<th style='text-align: left; padding: 8px;'>Summary</th>
																</tr>
															</thead>
																<tr style='border-bottom: 1px solid #eee;'>
																	<td style='padding: 8px;'><b><a href='https://github.com/ErikK81/KGates/blob/master/src/main/java/me/erik/kgates/listeners/PortalListener.java'>PortalListener.java</a></b></td>
																	<td style='padding: 8px;'>- Handles player interactions with portal gates by detecting proximity, verifying conditions, and managing teleportation between designated locations<br>- Ensures cooldowns to prevent rapid re-entry, executes custom commands upon activation, and provides visual and auditory effects to enhance user experience<br>- Integrates seamlessly within the broader gate management system to facilitate dynamic, condition-based teleportation across the server environment.</td>
																</tr>
															</table>
														</blockquote>
													</details>
													<!-- commands Submodule -->
													<details>
														<summary><b>commands</b></summary>
														<blockquote>
															<div class='directory-path' style='padding: 8px 0; color: #666;'>
																<code><b>⦿ src.main.java.me.erik.kgates.commands</b></code>
															<table style='width: 100%; border-collapse: collapse;'>
															<thead>
																<tr style='background-color: #f8f9fa;'>
																	<th style='width: 30%; text-align: left; padding: 8px;'>File Name</th>
																	<th style='text-align: left; padding: 8px;'>Summary</th>
																</tr>
															</thead>
																<tr style='border-bottom: 1px solid #eee;'>
																	<td style='padding: 8px;'><b><a href='https://github.com/ErikK81/KGates/blob/master/src/main/java/me/erik/kgates/commands/GateCommandExecutor.java'>GateCommandExecutor.java</a></b></td>
																	<td style='padding: 8px;'>- Facilitates command execution within the plugin architecture by enabling players or the console to trigger specific actions<br>- It dynamically interprets command strings, allowing for flexible command dispatching based on context, thereby supporting in-game interactions and administrative control<br>- This component integrates user commands seamlessly into the broader system, ensuring smooth operation of gate-related functionalities.</td>
																</tr>
															</table>
														</blockquote>
													</details>
												</blockquote>
											</details>
										</blockquote>
									</details>
								</blockquote>
							</details>
						</blockquote>
					</details>
				</blockquote>
			</details>
		</blockquote>
	</details>
</details>

---

## Getting Started

### Prerequisites

This project requires the following dependencies:

- **Programming Language:** Java
- **Package Manager:** Maven

### Installation

Build KGates from the source and install dependencies:

1. **Clone the repository:**

    ```sh
    ❯ git clone https://github.com/ErikK81/KGates
    ```

2. **Navigate to the project directory:**

    ```sh
    ❯ cd KGates
    ```

3. **Install the dependencies:**

**Using [maven](https://maven.apache.org/):**

```sh
❯ mvn install
```

### Usage

Run the project with:

**Using [maven](https://maven.apache.org/):**

```sh
mvn exec:java
```

### Testing

Kgates uses the {__test_framework__} test framework. Run the test suite with:

**Using [maven](https://maven.apache.org/):**

```sh
mvn test
```

---

## License

Kgates is protected under the [LICENSE](https://choosealicense.com/licenses) License. For more details, refer to the [LICENSE](https://choosealicense.com/licenses/) file.

---

## Acknowledgments

- Credit `contributors`, `inspiration`, `references`, etc.

<div align="left"><a href="#top">⬆ Return</a></div>

---
