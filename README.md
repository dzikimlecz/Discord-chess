# Discord-chess
Discord bot for playing chess with visualisation of the game

![image](https://github.com/dzikimlecz/Discord-chess/assets/67097253/0dea3bfe-e253-4ecc-bf4f-069b4a2d3c86)


# Configuration
The program requires a .env file containing discord app token and default command prefix. Example:  
> token=\<your token here\>  
> DEFAULT_PREFIX=--

# Usage
After adding it to the server you can start a game with command as such:  
>\--chess @user

By default you will play random color. To choose a side use -b (--black) or -w (--white) for yourself. Example:  
>\--chess @user -w

To make a move use command mv with standard chess notation as an argument. Example:  
>\--mv Qf6

If you wish to give up the game use this command:   
>\--resign

To get more information on those other commands use:
>\--help
