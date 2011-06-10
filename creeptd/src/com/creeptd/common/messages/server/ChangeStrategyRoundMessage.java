
package com.creeptd.common.messages.server;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.creeptd.common.messages.MessageUtil;


/**
 * Message from client, to update a tower.
 * 
 * @author andreas
 *
 */
public class ChangeStrategyRoundMessage extends ServerMessage implements GameMessage {

	
	/**
	*	regular expression for message-parsing.
	*/
	private static final String REGEXP_CHANGE_STRATEGY =
		"ROUND\\s([0-9]+):\\sPLAYER\\s([0-9]+)\\sCHANGES\\sTOWER\\s([0-9]+)\\s" +
		"TO\\s([^\"]+),([^\"]+)";
	
	
	/**
	 * pattern for regular expression.
	 */
	public static final Pattern PATTERN = Pattern.compile(REGEXP_CHANGE_STRATEGY);
	
	
	
	private Integer towerId;
	private String strategyName;
	private boolean locked;
	private Long roundId;
	private Integer playerId;

	/**
	 * @return the id of the tower to upgrade
	 */
	public Integer getTowerId() {
		return this.towerId;
	}

	/**
	 * @param towerId of the tower to upgrade
	 */
	public void setTowerId(Integer towerId) {
		this.towerId = towerId;
	}

	/**
	 * @param messageString the message as String.
	 */
	@Override
	public void initWithMessage(String messageString) {
		Matcher matcher = PATTERN.matcher(messageString);
		if (matcher.matches()) {
			this.setRoundId(Long.parseLong(matcher.group(1)));
			this.setPlayerId(Integer.parseInt(matcher.group(2)));
			this.setTowerId(Integer.valueOf(matcher.group(3)));
			this.setStrategyType(String.valueOf(matcher.group(4)));
			this.setLocked(Boolean.valueOf(matcher.group(5)));
		}		
		
	}
	
	/**
	 * @return the message as String.
	 */
	@Override
	public String getMessageString() {
		return "ROUND " + this.getRoundId() + ": PLAYER " + this.playerId
		+ " CHANGES TOWER " + this.getTowerId() + " TO "
		+ MessageUtil.prepareToSend(this.getStrategyType())  + "," 
		+ this.isLocked();
	}

	public void setStrategyType(String strategyName) {
		// TODO Auto-generated method stub
		this.strategyName = strategyName;
	}
	
	public String getStrategyType(){
		return this.strategyName;
	}
	
	public void setLocked(boolean locked){
		this.locked = locked;
	}
	
	public boolean isLocked(){
		return this.locked;
	}

	/**
	 * @return the roundId
	 */
	public Long getRoundId() {
		return this.roundId;
	}

	/**
	 * @param roundId the roundId to set
	 */
	public void setRoundId(Long roundId) {
		this.roundId = roundId;
	}

	/**
	 * @return the playerId
	 */
	public Integer getPlayerId() {
		return this.playerId;
	}

	/**
	 * @param playerId the playerId to set
	 */
	public void setPlayerId(Integer playerId) {
		this.playerId = playerId;
	}
}
