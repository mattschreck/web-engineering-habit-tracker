<script lang="ts">
	import { onMount } from 'svelte';
	import { goto } from '$app/navigation';
	import type { HabitResponse, HabitLogResponse } from '$lib/api/client';
	import {
		isAuthenticated,
		getHabits,
		getHabitLogsInRange,
		deleteHabit,
		ApiError
	} from '$lib/api/client';
	import { getLocalDateString } from '$lib/utils/habit-progress';
	import HabitCard from '$lib/components/habit-card.svelte';
	import AddHabitCard from '$lib/components/add-habit-card.svelte';
	import HabitDialog from '$lib/components/habit-dialog.svelte';
	import Navigation from '$lib/components/navigation.svelte';
	import { BackgroundBeams } from '$lib/components/ui/background-beams';

	let habits = $state<HabitResponse[]>([]);
	let habitLogs = $state<Map<number, HabitLogResponse[]>>(new Map());
	let loading = $state(true);
	let error = $state<string | null>(null);

	// Dialog states
	let showDialog = $state(false);
	let editingHabit = $state<HabitResponse | null>(null);

	onMount(() => {
		if (!isAuthenticated()) {
			goto('/login');
			return;
		}
		loadHabits();
	});

	// Habits und Logs laden
	async function loadHabits() {
		try {
			loading = true;
			error = null;
			habits = await getHabits();

			// Logs der letzten 90 Tage laden (für alle Frequenzen)
			const today = new Date();
			const ninetyDaysAgo = new Date(today);
			ninetyDaysAgo.setDate(ninetyDaysAgo.getDate() - 90);

			const endDate = getLocalDateString(today);
			const startDate = getLocalDateString(ninetyDaysAgo);

			const logsMap = new Map<number, HabitLogResponse[]>();
			for (const habit of habits) {
				try {
					const logs = await getHabitLogsInRange(habit.id, startDate, endDate);
					logsMap.set(habit.id, logs);
				} catch {
					logsMap.set(habit.id, []);
				}
			}
			habitLogs = logsMap;
		} catch (err) {
			if (err instanceof ApiError) {
				error = err.error;
			} else {
				error = 'Fehler beim Laden der Habits';
			}
		} finally {
			loading = false;
		}
	}

	function handleAddHabit() {
		editingHabit = null;
		showDialog = true;
	}

	function handleEditHabit(habit: HabitResponse) {
		editingHabit = habit;
		showDialog = true;
	}

	// Habit löschen
	async function handleDeleteClick(habit: HabitResponse) {
		const confirmed = confirm(
			`"${habit.name}" wirklich löschen? Alle zugehörigen Logs werden ebenfalls gelöscht. Diese Aktion kann nicht rückgängig gemacht werden.`
		);

		if (!confirmed) return;

		try {
			await deleteHabit(habit.id);
			await loadHabits();
		} catch (err) {
			if (err instanceof ApiError) {
				error = err.error;
			} else {
				error = 'Fehler beim Löschen des Habits';
			}
			alert(`Fehler: ${error}`);
		}
	}

	function handleDialogSuccess() {
		loadHabits();
	}
</script>

<svelte:head>
	<title>Dashboard - Habit Tracker</title>
</svelte:head>

<div class="relative min-h-screen overflow-hidden bg-background">
	<BackgroundBeams />

	<div class="relative z-10 min-h-screen">
		<Navigation />

		<!-- Hauptinhalt -->
		<main class="mx-auto max-w-7xl px-4 py-8 sm:px-6 lg:px-8">
			<div class="mb-8">
				<h1 class="text-3xl font-bold text-foreground">Meine Habits</h1>
				<p class="mt-2 text-muted-foreground">Verfolge deinen Fortschritt und baue bessere Gewohnheiten auf</p>
			</div>

			{#if loading}
				<div class="flex min-h-[100] items-center justify-center">
					<p class="text-muted-foreground">Laden...</p>
				</div>
			{:else if error}
				<div class="rounded-md bg-red-50 p-4 text-red-500 dark:bg-red-950/20">
					{error}
				</div>
			{:else}
				<div class="grid gap-6 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
					<!-- Neues Habit -->
					<AddHabitCard onClick={handleAddHabit} />

					<!-- Habit-Karten -->
					{#each habits as habit (habit.id)}
						<HabitCard
							{habit}
							logs={habitLogs.get(habit.id) || []}
							onEdit={() => handleEditHabit(habit)}
							onDelete={() => handleDeleteClick(habit)}
							onCheckIn={loadHabits}
						/>
					{/each}
				</div>

				{#if habits.length === 0}
					<div class="mt-8 text-center">
						<p class="text-muted-foreground">
							Noch keine Habits. Erstelle dein erstes Habit um loszulegen!
						</p>
					</div>
				{/if}
			{/if}
		</main>
	</div>
</div>

<!-- Habit Dialog -->
<HabitDialog bind:open={showDialog} habit={editingHabit} onSuccess={handleDialogSuccess} />
