package uk.co.harieo.StagePlay.utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ReportResult {

	private String name;

	private int amountOfErrors = 0;
	private int amountOfWarnings = 0;
	private List<String> transcript = new ArrayList<>();

	/**
	 * A representation of a set of checks/validations, which is formatted, can be sent to a player and can be used by
	 * other systems to determine the status of something
	 *
	 * @param reportName which will be used when represented to a player
	 */
	public ReportResult(String reportName) {
		this.name = reportName;
	}

	/**
	 * Add a message which reports the result of a check and updates the relevant values in the report
	 *
	 * @param message describing the check performed
	 * @param type the result of the check
	 */
	public void addCheckMessage(String message, ResultType type) {
		transcript.add(type.getColor() + message);
		if (type == ResultType.WARN) {
			amountOfWarnings++;
		} else if (type == ResultType.ERROR) {
			amountOfErrors++;
		}
	}

	/**
	 * Sends a log of this report and all checks performed to a player for inspection
	 *
	 * @param player to send the report to
	 */
	public void sendReport(Player player) {
		player.sendMessage(ChatColor.GRAY + "Result of " + ChatColor.YELLOW + name);

		String resultMessage = (hasEncounteredError() ?
				ChatColor.RED + "The report encountered " + amountOfErrors + " error(s) and "
				: ChatColor.GREEN + "The report encountered no errors and ") +
				(hasEncounteredWarning() ?
						ChatColor.YELLOW + String.valueOf(amountOfWarnings) + " warning(s)"
						: "no warnings");

		player.sendMessage(resultMessage);
		player.sendMessage(""); // Split the report up for readability

		if (hasEncounteredError() || hasEncounteredWarning()) { // Prevent spamming for successful reports
			player.sendMessage(ChatColor.GRAY + "The report transcript is as follows:");
			for (String message : transcript) {
				player.sendMessage(message); // Already coloured transcript
			}
		}
	}

	/**
	 * @return whether any checks on the report are errors
	 */
	public boolean hasEncounteredError() {
		return amountOfErrors > 0;
	}

	/**
	 * @return whether any checks on the report are warnings
	 */
	public boolean hasEncounteredWarning() {
		return amountOfWarnings > 0;
	}

	public enum ResultType {
		SUCCESS(ChatColor.GREEN),
		INFO(ChatColor.GRAY),
		WARN(ChatColor.YELLOW),
		ERROR(ChatColor.RED);

		private ChatColor color;

		/**
		 * A type of result that could occur from a check
		 *
		 * @param color to be used when represented to a player
		 */
		ResultType(ChatColor color) {
			this.color = color;
		}

		/**
		 * @return the colour to format the description of this result type to a player
		 */
		public ChatColor getColor() {
			return color;
		}

	}
}
