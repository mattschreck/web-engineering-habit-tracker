<script lang="ts">
	import type { HabitResponse, HabitLogResponse } from '$lib/api/client';
	import { createHabitLog, updateHabitLog, ApiError } from '$lib/api/client';
	import { CircularProgress } from '$lib/components/ui/circular-progress';
	import * as Card from '$lib/components/ui/card';
	import { Button } from '$lib/components/ui/button';
	import { Pencil, Trash2, Calendar, CalendarX, Check } from 'lucide-svelte';
	import { calculateHabitProgress, formatFrequency } from '$lib/utils/habit-progress';

	interface Props {
		habit: HabitResponse;
		logs?: HabitLogResponse[];
		onEdit?: () => void;
		onDelete?: () => void;
		onCheckIn?: () => void;
	}

	let { habit, logs = [], onEdit, onDelete, onCheckIn }: Props = $props();

	let progress = $derived(calculateHabitProgress(habit.frequency, logs));
	let frequencyText = $derived(formatFrequency(habit.frequency));
	let isCheckingIn = $state(false);

	// Check if today is already logged
	let todayLog = $derived.by(() => {
		const today = new Date().toISOString().split('T')[0];
		return logs.find((log) => log.logDate.split('T')[0] === today);
	});

	let isCompletedToday = $derived(todayLog?.completed ?? false);

	function formatDate(dateString: string | null): string {
		if (!dateString) return '';
		const date = new Date(dateString);
		return date.toLocaleDateString('en-US', {
			month: 'short',
			day: 'numeric',
			year: 'numeric'
		});
	}

	async function handleCheckIn() {
		isCheckingIn = true;
		try {
			const today = new Date().toISOString().split('T')[0];

			if (todayLog) {
				// Update existing log - toggle completion
				await updateHabitLog(todayLog.id, {
					habitId: habit.id,
					logDate: today,
					completed: !todayLog.completed
				});
			} else {
				// Create new log
				await createHabitLog({
					habitId: habit.id,
					logDate: today,
					completed: true
				});
			}

			onCheckIn?.();
		} catch (error) {
			console.error('Failed to check in habit:', error);
			if (error instanceof ApiError) {
				alert(`Failed to check in: ${error.error}`);
			}
		} finally {
			isCheckingIn = false;
		}
	}
</script>

<Card.Root class="w-full">
	<Card.Header>
		<div class="flex items-start justify-between">
			<div class="flex-1">
				<Card.Title class="text-lg">{habit.name}</Card.Title>
				{#if habit.description}
					<Card.Description class="mt-1 text-sm">{habit.description}</Card.Description>
				{/if}
			</div>
		</div>
	</Card.Header>

	<Card.Content class="flex flex-col items-center gap-4">
		<CircularProgress {progress} size={100} strokeWidth={8} />
		<div class="w-full space-y-2">
			<div class="text-center">
				<p class="text-sm font-medium text-muted-foreground">{frequencyText}</p>
			</div>

			<!-- Start/End Date Information -->
			{#if habit.startDate || habit.endDate}
				<div class="flex flex-col gap-1 text-xs text-muted-foreground">
					{#if habit.startDate}
						<div class="flex items-center gap-1.5">
							<Calendar class="h-3.5 w-3.5" />
							<span>Starts: {formatDate(habit.startDate)}</span>
						</div>
					{/if}
					{#if habit.endDate}
						<div class="flex items-center gap-1.5">
							<CalendarX class="h-3.5 w-3.5" />
							<span>Ends: {formatDate(habit.endDate)}</span>
						</div>
					{:else if habit.startDate}
						<div class="flex items-center gap-1.5">
							<CalendarX class="h-3.5 w-3.5" />
							<span>No end date</span>
						</div>
					{/if}
				</div>
			{/if}
		</div>
	</Card.Content>

	<Card.Footer class="flex justify-between gap-2">
		<div class="flex gap-2">
			{#if onEdit}
				<Button variant="outline" size="icon" onclick={onEdit} title="Edit habit">
					<Pencil class="h-4 w-4" />
				</Button>
			{/if}
			{#if onDelete}
				<Button variant="outline" size="icon" onclick={onDelete} title="Delete habit">
					<Trash2 class="h-4 w-4" />
				</Button>
			{/if}
		</div>

		<!-- Check-In Button -->
		<Button
			variant={isCompletedToday ? 'default' : 'outline'}
			onclick={handleCheckIn}
			disabled={isCheckingIn}
			class="min-w-24"
		>
			<Check class="mr-2 h-4 w-4" />
			{isCheckingIn ? 'Saving...' : isCompletedToday ? 'Done' : 'Check In'}
		</Button>
	</Card.Footer>
</Card.Root>
