package uk.co.harieo.StagePlay.utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ReportResult {

	private String name;

	private boolean hasEncounteredError = false;
	private List<String> transcript = new ArrayList<>();

	public ReportResult(String reportName) {
		this.name = reportName;
	}

	public void addSuccessMessage(String message) {
		transcript.add(ChatColor.GREEN + message);
	}

	public void addInfoMessage(String message) {
		transcript.add(ChatColor.GRAY + message);
	}

	public void addErrorMessage(String message) {
		transcript.add(ChatColor.RED + message);
		if (!hasEncounteredError) {
			hasEncounteredError = true;
		}
	}

	public void sendReport(Player player) {
		player.sendMessage(ChatColor.GRAY + "Result of " + ChatColor.YELLOW + name);
		player.sendMessage(hasEncounteredError ? ChatColor.RED + "The report encountered an error"
				: ChatColor.GREEN + "The report encountered no errors");

		if (hasEncounteredError) { // Prevent spamming for successful reports
			player.sendMessage(ChatColor.GRAY + "The report transcript is as follows:");
			for (String message : transcript) {
				player.sendMessage(message); // Already coloured transcript
			}
		}
	}

}
