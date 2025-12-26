import type { HabitFrequency, HabitLogResponse } from '$lib/api/client';

/**
 * Calculate habit completion progress based on logs
 * @param frequency - DAILY (last 7 days) or WEEKLY (last 4 weeks)
 * @param logs - Array of habit logs
 * @returns Progress percentage (0-100)
 */
export function calculateHabitProgress(
	frequency: HabitFrequency,
	logs: HabitLogResponse[]
): number {
	const today = new Date();
	today.setHours(0, 0, 0, 0);

	let daysToCheck: number;
	switch (frequency) {
		case 'DAILY':
			daysToCheck = 7; // Last 7 days
			break;
		case 'WEEKLY':
			daysToCheck = 28; // Last 4 weeks (28 days)
			break;
		case 'MONTHLY':
			daysToCheck = 90; // Last 3 months (90 days)
			break;
		default:
			daysToCheck = 7; // Default to weekly
	}

	// Generate array of dates to check
	const datesToCheck: string[] = [];
	for (let i = 0; i < daysToCheck; i++) {
		const date = new Date(today);
		date.setDate(date.getDate() - i);
		datesToCheck.push(date.toISOString().split('T')[0]);
	}

	// Count completed logs
	const completedDates = new Set(
		logs.filter((log) => log.completed).map((log) => log.logDate.split('T')[0])
	);

	const completedCount = datesToCheck.filter((date) => completedDates.has(date)).length;

	return Math.round((completedCount / daysToCheck) * 100);
}

/**
 * Get the number of days/periods to display for a given frequency
 */
export function getFrequencyPeriodText(frequency: HabitFrequency): string {
	switch (frequency) {
		case 'DAILY':
			return 'Last 7 days';
		case 'WEEKLY':
			return 'Last 4 weeks';
		case 'MONTHLY':
			return 'Last 3 months';
		default:
			return 'Progress';
	}
}

/**
 * Format frequency enum to human-readable text
 */
export function formatFrequency(frequency: HabitFrequency): string {
	switch (frequency) {
		case 'DAILY':
			return 'Daily';
		case 'WEEKLY':
			return 'Weekly';
		case 'MONTHLY':
			return 'Monthly';
		case 'CUSTOM':
			return 'Custom';
		default:
			return frequency;
	}
}
