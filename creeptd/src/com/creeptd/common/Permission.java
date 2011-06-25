package com.creeptd.common;

public interface Permission {

	int MOD_GLOBAL = 1 << 0;

	int KICK = 1 << 1;

	int KICK_IMMUN = 1 << 2;

	int BAN = 1 << 3;

	int BAN_IMMUNE = 1 << 4;

	int UNBAN = 1 << 5;

	int NO_TIMEOUT = 1 << 6;

	int LEAGUE_MOD = 1 << 7;


}
