<script lang="ts">
	import type { HabitResponse, HabitLogResponse } from '$lib/api/client';
	import { createHabitLog, updateHabitLog, ApiError } from '$lib/api/client';
	import * as Card from '$lib/components/ui/card';
	import { Button } from '$lib/components/ui/button';
	import { Pencil, Trash2, Check, Flame } from 'lucide-svelte';
	import {
		formatFrequency,
		calculateCurrentStreak,
		calculateTotalExpected,
		calculateTotalCompleted,
		getLocalDateString
	} from '$lib/utils/habit-progress';

	interface Props {
		habit: HabitResponse;
		logs?: HabitLogResponse[];
		onEdit?: () => void;
		onDelete?: () => void;
		onCheckIn?: () => void;
	}

	let { habit, logs = [], onEdit, onDelete, onCheckIn }: Props = $props();

	// Berechnete Werte
	let frequencyText = $derived(formatFrequency(habit.frequency));
	let currentStreak = $derived(calculateCurrentStreak(logs));
	let totalExpected = $derived(
		calculateTotalExpected(habit.frequency, habit.startDate, habit.endDate)
	);
	let totalCompleted = $derived(calculateTotalCompleted(logs));
	let isCheckingIn = $state(false);

	// Heutiges Log suchen
	let todayLog = $derived.by(() => {
		const today = getLocalDateString();
		return logs.find((log) => log.logDate.split('T')[0] === today);
	});

	let isCompletedToday = $derived(todayLog !== undefined && todayLog.completed === true);

	// Habit für heute abhaken/umschalten
	async function handleCheckIn() {
		isCheckingIn = true;
		try {
			const today = getLocalDateString();

			if (todayLog) {
				// Bestehendes Log aktualisieren (Toggle)
				await updateHabitLog(todayLog.id, {
					habitId: habit.id,
					logDate: today,
					completed: !todayLog.completed,
					notes: todayLog.notes || null
				});
			} else {
				// Neues Log erstellen
				await createHabitLog({
					habitId: habit.id,
					logDate: today,
					completed: true,
					notes: null
				});
			}

			await onCheckIn?.();
		} catch (error) {
			console.error('Check-In fehlgeschlagen:', error);
			if (error instanceof ApiError) {
				alert(`Fehler: ${error.error}`);
			}
		} finally {
			isCheckingIn = false;
		}
	}
</script>

<Card.Root class="w-full">
	<Card.Header>
		<div class="flex items-center justify-between gap-2">
			<Card.Title class="text-lg">{habit.name}</Card.Title>
			<span
				class="inline-flex items-center rounded-full border border-border bg-secondary px-2.5 py-0.5 text-xs font-semibold text-secondary-foreground"
			>
				{frequencyText}
			</span>
		</div>
	</Card.Header>

	<Card.Content class="flex flex-col items-center gap-3 py-6">
		{#if totalExpected !== null}
			<!-- Mit Enddatum: Zeige Fortschritt -->
			<div class="flex items-center gap-2">
				<span class="text-5xl font-bold">{totalCompleted}</span>
				<span class="text-3xl text-muted-foreground">/</span>
				<span class="text-3xl text-muted-foreground">{totalExpected}</span>
			</div>
			<p class="text-sm text-muted-foreground">Erledigt</p>
		{:else}
			<!-- Ohne Enddatum: Zeige Streak -->
			<div class="flex items-center gap-3">
				<Flame class="h-10 w-10 text-orange-500" />
				<span class="text-5xl font-bold">{currentStreak}</span>
			</div>
			<p class="text-sm text-muted-foreground">Tage hintereinander</p>
		{/if}
	</Card.Content>

	<Card.Footer class="flex justify-between gap-2">
		<div class="flex gap-2">
			{#if onEdit}
				<Button variant="outline" size="icon" onclick={onEdit} title="Bearbeiten">
					<Pencil class="h-4 w-4" />
				</Button>
			{/if}
			{#if onDelete}
				<Button variant="outline" size="icon" onclick={onDelete} title="Löschen">
					<Trash2 class="h-4 w-4" />
				</Button>
			{/if}
		</div>

		<!-- Check-In Button -->
		<Button
			variant={isCompletedToday ? 'default' : 'outline'}
			onclick={handleCheckIn}
			disabled={isCheckingIn}
			class="min-w-28"
		>
			<Check class="mr-2 h-4 w-4" />
			{isCheckingIn ? 'Speichern...' : isCompletedToday ? '✓ Erledigt' : 'Abhaken'}
		</Button>
	</Card.Footer>
</Card.Root>
