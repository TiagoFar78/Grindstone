List of thins to have in consideration when writing code:
- Write as few comments as possible. Preferably none. The same goes for javadocs, avoid them as much as you can. If I want to have some I will request it.
- Don't ever use the package definition of a class outside of the import block. If there are two different types with the same name, change the name of the one that is "deeper" in the hierarchy by adding a prefix to it. For example if you have a Player type used for a concrete game, that will conflict with the top level Player type. In that case add the initials of the concrete game to the type prefix.
- There are supposed to be empty lines after the closing bracket of an if. Especially if it was already done when you were going to change it.
