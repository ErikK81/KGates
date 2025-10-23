<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>KGates Plugin Documentation</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            line-height: 1.6;
            max-width: 900px;
            margin: auto;
            padding: 20px;
            background: #f4f4f4;
            color: #333;
        }
        h1, h2, h3 {
            color: #2c3e50;
        }
        code {
            background: #eee;
            padding: 2px 5px;
            border-radius: 3px;
        }
        pre {
            background: #eee;
            padding: 10px;
            border-radius: 5px;
            overflow-x: auto;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 20px;
        }
        table, th, td {
            border: 1px solid #ccc;
        }
        th, td {
            padding: 8px 12px;
            text-align: left;
        }
        th {
            background: #ecf0f1;
        }
        ul {
            padding-left: 20px;
        }
        .command {
            background: #2980b9;
            color: #fff;
            padding: 2px 5px;
            border-radius: 3px;
        }
    </style>
</head>
<body>

<h1>KGates</h1>
<p><strong>KGates</strong> is a Minecraft plugin that allows server admins to create, customize, and manage teleportation gates with conditions like player permissions, health, time, or weather. Gates can be configured via an intuitive GUI or chat input.</p>

<hr>

<h2>Features</h2>
<ul>
    <li>Create and edit custom gates with unique IDs.</li>
    <li>Set gate positions (two points) and configure detection radius.</li>
    <li>Apply conditions to gates:
        <ul>
            <li><strong>Permission:</strong> Only players with a specific permission can use the gate.</li>
            <li><strong>Health:</strong> Requires the player to have a minimum health value.</li>
            <li><strong>Time:</strong> Restrict gate usage to a specific in-game time range.</li>
            <li><strong>Weather:</strong> Restrict gate usage to certain weather conditions.</li>
        </ul>
    </li>
    <li>Cooldown system to prevent repeated teleportation abuse.</li>
    <li>User-friendly GUI for creating and editing gates.</li>
    <li>Command-based and GUI-based management.</li>
    <li>Tab-completion support for commands.</li>
</ul>

<hr>

<h2>Commands</h2>
<p>All commands require the <code>kgates.admin</code> permission.</p>
<table>
    <thead>
        <tr>
            <th>Command</th>
            <th>Description</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td><span class="command">/kgate create &lt;id&gt;</span></td>
            <td>Starts creating a new gate with the specified ID.</td>
        </tr>
        <tr>
            <td><span class="command">/kgate remove &lt;id&gt;</span></td>
            <td>Removes an existing gate by ID.</td>
        </tr>
        <tr>
            <td><span class="command">/kgate edit &lt;id&gt;</span></td>
            <td>Opens the GUI to edit a gate.</td>
        </tr>
        <tr>
            <td><span class="command">/kgate browse</span></td>
            <td>Opens the list of all gates. *(GUI coming soon)*</td>
        </tr>
        <tr>
            <td><span class="command">/kgate go &lt;id&gt; &lt;1/2&gt;</span></td>
            <td>Teleports the player to one of the gate points.</td>
        </tr>
    </tbody>
</table>

<hr>

<h2>Installation</h2>
<ol>
    <li>Download the latest <code>KGates.jar</code>.</li>
    <li>Place it in your server's <code>plugins</code> folder.</li>
    <li>Start or restart your server.</li>
    <li>Ensure your admins have the <code>kgates.admin</code> permission.</li>
</ol>

<hr>

<h2>Using KGates</h2>

<h3>Creating a Gate</h3>
<ol>
    <li>Run <code>/kgate create &lt;id&gt;</code> in-game.</li>
    <li>A GUI will open to configure the gate.</li>
    <li>Set Point A and Point B in the world.</li>
    <li>Configure optional properties: detection radius, cooldown, and conditions (via GUI or chat).</li>
    <li>Save the gate to finalize.</li>
</ol>

<h3>Editing a Gate</h3>
<ol>
    <li>Run <code>/kgate edit &lt;id&gt;</code> in-game.</li>
    <li>The GUI opens with all gate settings.</li>
    <li>Modify locations, type, cooldown, radius, and conditions.</li>
    <li>Save changes.</li>
</ol>

<h3>Teleporting</h3>
<p>Use <code>/kgate go &lt;id&gt; &lt;1/2&gt;</code> to teleport to a gate's location for setup or debugging.</p>

<hr>

<h2>Conditions</h2>
<p>Gates can have one or more conditions:</p>
<ul>
    <li><strong>Permission:</strong> Gate only usable by players with a certain permission node.</li>
    <li><strong>Health:</strong> Requires a minimum player health value.</li>
    <li><strong>Time:</strong> Restrict gate usage to an in-game time range (e.g., 6000-18000 ticks).</li>
    <li><strong>Weather:</strong> Restrict gate usage to specific weather conditions (e.g., rain, clear).</li>
</ul>
<p>Conditions can be added, edited, or removed via the GUI. Some require input through the chat.</p>

<hr>

<h2>Configuration</h2>
<p>Currently, KGates saves gates in memory. Persistent storage (YAML/JSON/Database) can be added in future updates.</p>

<hr>

<h2>Contributing</h2>
<p>Contributions are welcome! You can:</p>
<ul>
    <li>Report bugs</li>
    <li>Suggest new features</li>
    <li>Improve the code or documentation</li>
</ul>
<p>Open a GitHub issue or submit a pull request.</p>

<hr>

<h2>License</h2>
<p>KGates is <strong>open source</strong>. You are free to fork, modify, or redistribute under your server rules.</p>

<hr>
</body>
</html>
