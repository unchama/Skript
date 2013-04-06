/*
 *   This file is part of Skript.
 *
 *  Skript is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Skript is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Skript.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * 
 * Copyright 2011-2013 Peter Güttinger
 * 
 */

package ch.njol.skript.expressions;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.classes.Converter;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.effects.Delay;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.Utils;
import ch.njol.util.Kleenean;

/**
 * @author Peter Güttinger
 */
@SuppressWarnings("serial")
@Name("Game Mode")
@Description("The gamemode of a player.")
@Examples({"player's gamemode is survival",
		"set the player's gamemode to creative"})
@Since("1.0")
public class ExprGameMode extends PropertyExpression<Player, GameMode> {
	
	static {
		Skript.registerExpression(ExprGameMode.class, GameMode.class, ExpressionType.PROPERTY, "[the] game[ ]mode of %players%", "%players%'[s] game[ ]mode");
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean init(final Expression<?>[] vars, final int matchedPattern, final Kleenean isDelayed, final ParseResult parser) {
		setExpr((Expression<Player>) vars[0]);
		return true;
	}
	
	@Override
	public String toString(final Event e, final boolean debug) {
		return "the gamemode of " + getExpr().toString(e, debug);
	}
	
	@Override
	protected GameMode[] get(final Event e, final Player[] source) {
		if (e instanceof PlayerGameModeChangeEvent && getTime() >= 0 && getExpr().isDefault() && !Delay.isDelayed(e)) {
			return new GameMode[] {((PlayerGameModeChangeEvent) e).getNewGameMode()};
		}
		return get(source, new Converter<Player, GameMode>() {
			@Override
			public GameMode convert(final Player p) {
				return p.getGameMode();
			}
		});
	}
	
	@Override
	public Class<GameMode> getReturnType() {
		return GameMode.class;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Class<?>[] acceptChange(final ChangeMode mode) {
		if (mode == ChangeMode.SET)
			return Utils.array(GameMode.class);
		return null;
	}
	
	@Override
	public void change(final Event e, final Object delta, final ChangeMode mode) throws UnsupportedOperationException {
		final GameMode m = (GameMode) delta;
		for (final Player p : getExpr().getArray(e))
			p.setGameMode(m);
	}
	
	@Override
	public boolean setTime(final int time) {
		return super.setTime(time, PlayerGameModeChangeEvent.class, getExpr());
	}
}