rootProject.name = extra.properties["name"] as String

include(
    "anfodis-core",
    "anfodis-command",
    "anfodis-command-completion",
    "anfodis-listener",
    "anfodis-jda",
    "anfodis-paper",
    "anfodis-velocity",
    "anfodis-waterfall"
)
