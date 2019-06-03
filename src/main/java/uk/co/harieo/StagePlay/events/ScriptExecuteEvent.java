package uk.co.harieo.StagePlay.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import uk.co.harieo.StagePlay.entities.ScriptedEntity;
import uk.co.harieo.StagePlay.scripts.ScriptExecutor;
import uk.co.harieo.StagePlay.scripts.StagedScript;

public class ScriptExecuteEvent extends Event {

	private ScriptExecutor scriptExecutor;
	private ScriptedEntity entity;
	private StagedScript script;
	private Player player;

	public ScriptExecuteEvent(ScriptExecutor executor, ScriptedEntity entity, StagedScript script, Player player) {
		this.scriptExecutor = executor;
		this.entity = entity;
		this.script = script;
		this.player = player;
	}

	public ScriptExecutor getScriptExecutor() {
		return scriptExecutor;
	}

	public ScriptedEntity getEntity() {
		return entity;
	}

	public StagedScript getScript() {
		return script;
	}

	public Player getExecutingPlayer() {
		return player;
	}

	private static final HandlerList handlers = new HandlerList();

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
