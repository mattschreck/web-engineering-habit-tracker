import type { HabitFrequency, HabitLogResponse } from '$lib/api/client';

// Heutiges Datum im lokalen Zeitzone-Format (YYYY-MM-DD)
export function getLocalDateString(date: Date = new Date()): string {
	const year = date.getFullYear();
	const month = String(date.getMonth() + 1).padStart(2, '0');
	const day = String(date.getDate()).padStart(2, '0');
	return `${year}-${month}-${day}`;
}

// Frequenz-Text formatieren (Enum → Lesbar)
export function formatFrequency(frequency: HabitFrequency): string {
	const translations = {
		DAILY: 'Täglich',
		WEEKLY: 'Wöchentlich',
		MONTHLY: 'Monatlich',
		CUSTOM: 'Benutzerdefiniert'
	};
	return translations[frequency] || frequency;
}

// Aktuelle Streak berechnen (aufeinanderfolgende Tage)
export function calculateCurrentStreak(logs: HabitLogResponse[]): number {
	if (logs.length === 0) return 0;

	// Nur abgeschlossene Logs als Set speichern (schnelles Lookup)
	const completedDates = new Set(
		logs.filter((log) => log.completed).map((log) => log.logDate.split('T')[0])
	);

	if (completedDates.size === 0) return 0;

	let streak = 0;
	let currentDate = new Date();

	// Von heute rückwärts zählen
	while (streak < 1000) {
		const dateStr = getLocalDateString(currentDate);
		if (completedDates.has(dateStr)) {
			streak++;
			currentDate.setDate(currentDate.getDate() - 1);
		} else {
			break; // Streak unterbrochen
		}
	}

	return streak;
}

// Erwartete Gesamtanzahl basierend auf Zeitraum (null = unbegrenzt)
export function calculateTotalExpected(
	frequency: HabitFrequency,
	startDate: string | null,
	endDate: string | null
): number | null {
	if (!endDate) return null; // Unbegrenzt

	const start = startDate ? new Date(startDate) : new Date();
	const end = new Date(endDate);
	const daysDiff = Math.floor((end.getTime() - start.getTime()) / (1000 * 60 * 60 * 24)) + 1;

	switch (frequency) {
		case 'DAILY':
			return daysDiff;
		case 'WEEKLY':
			return Math.ceil(daysDiff / 7);
		case 'MONTHLY':
			const monthsDiff =
				(end.getFullYear() - start.getFullYear()) * 12 + (end.getMonth() - start.getMonth()) + 1;
			return monthsDiff;
		default:
			return daysDiff;
	}
}

// Anzahl erledigter Logs
export function calculateTotalCompleted(logs: HabitLogResponse[]): number {
	return logs.filter((log) => log.completed).length;
}
