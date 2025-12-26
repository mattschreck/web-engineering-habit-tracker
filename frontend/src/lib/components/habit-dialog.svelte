<script lang="ts">
	import type { HabitResponse, HabitRequest, HabitFrequency } from '$lib/api/client';
	import { createHabit, updateHabit, ApiError } from '$lib/api/client';
	import * as Dialog from '$lib/components/ui/dialog';
	import { Button } from '$lib/components/ui/button';
	import { Input } from '$lib/components/ui/input';
	import { Label } from '$lib/components/ui/label';
	import * as Select from '$lib/components/ui/select';
	import { formatFrequency } from '$lib/utils/habit-progress';

	let {
		open = $bindable(false),
		habit = null,
		onSuccess = undefined
	}: {
		open?: boolean;
		habit?: HabitResponse | null;
		onSuccess?: (() => void) | undefined;
	} = $props();

	let name = $state('');
	let description = $state('');
	let frequency: HabitFrequency = $state('DAILY');
	let startDate = $state('');
	let endDate = $state('');
	let loading = $state(false);
	let error = $state<string | null>(null);

	let isEdit = $derived(habit !== null);
	let dialogTitle = $derived(isEdit ? 'Edit Habit' : 'Create New Habit');
	let submitButtonText = $derived(isEdit ? 'Update' : 'Create');

	// Initialize form when habit changes
	$effect(() => {
		if (habit) {
			name = habit.name;
			description = habit.description || '';
			frequency = habit.frequency;
			startDate = habit.startDate || '';
			endDate = habit.endDate || '';
		} else {
			name = '';
			description = '';
			frequency = 'DAILY';
			startDate = '';
			endDate = '';
		}
		error = null;
	});

	async function handleSubmit(event?: Event) {
		event?.preventDefault();
		error = null;
		loading = true;

		try {
			const data: HabitRequest = {
				name: name.trim(),
				description: description.trim() || null,
				frequency,
				startDate: startDate || null,
				endDate: endDate || null,
				active: true
			};

			if (isEdit && habit) {
				await updateHabit(habit.id, data);
			} else {
				await createHabit(data);
			}

			open = false;
			onSuccess?.();
		} catch (err) {
			if (err instanceof ApiError) {
				error = err.error;
			} else {
				error = 'An unexpected error occurred';
			}
		} finally {
			loading = false;
		}
	}

	function handleCancel() {
		open = false;
		error = null;
	}
</script>

<Dialog.Root bind:open>
	<Dialog.Content class="sm:max-w-[425px]">
		<Dialog.Header>
			<Dialog.Title>{dialogTitle}</Dialog.Title>
			<Dialog.Description>
				{isEdit ? 'Update your habit details.' : 'Create a new habit to track.'}
			</Dialog.Description>
		</Dialog.Header>

		<form onsubmit={handleSubmit} class="space-y-4">
			<!-- Name Input -->
			<div class="grid gap-2">
				<Label for="name">Name *</Label>
				<Input
					id="name"
					type="text"
					placeholder="E.g., Morning Exercise"
					bind:value={name}
					autocomplete="off"
					required
					disabled={loading}
					maxlength={100}
				/>
			</div>

			<!-- Description Input -->
			<div class="grid gap-2">
				<Label for="description">Description</Label>
				<Input
					id="description"
					type="text"
					placeholder="Optional description"
					bind:value={description}
					autocomplete="off"
					disabled={loading}
					maxlength={500}
				/>
			</div>

			<!-- Frequency Select -->
			<div class="grid gap-2">
				<Label for="frequency">Frequency *</Label>
				<Select.Root
					selected={{ value: frequency, label: formatFrequency(frequency) }}
					onSelectedChange={(v: any) => {
						if (v) frequency = v.value as HabitFrequency;
					}}
				>
					<Select.Trigger id="frequency" disabled={loading}>
						<Select.Value placeholder="Select frequency" />
					</Select.Trigger>
					<Select.Content>
						<Select.Item value="DAILY">Daily</Select.Item>
						<Select.Item value="WEEKLY">Weekly</Select.Item>
						<Select.Item value="MONTHLY">Monthly</Select.Item>
					</Select.Content>
				</Select.Root>
			</div>

			<!-- Start Date Input -->
			<div class="grid gap-2">
				<Label for="startDate">Start Date</Label>
				<Input
					id="startDate"
					type="date"
					bind:value={startDate}
					autocomplete="off"
					disabled={loading}
				/>
			</div>

			<!-- End Date Input -->
			<div class="grid gap-2">
				<Label for="endDate">End Date</Label>
				<Input
					id="endDate"
					type="date"
					bind:value={endDate}
					autocomplete="off"
					disabled={loading}
					min={startDate || undefined}
				/>
				<p class="text-xs text-muted-foreground">
					Leave empty for unlimited duration
				</p>
			</div>

			<!-- Error Message -->
			{#if error}
				<div class="rounded-md bg-red-50 p-3 text-sm text-red-500 dark:bg-red-950/20">
					{error}
				</div>
			{/if}

			<!-- Action Buttons -->
			<Dialog.Footer>
				<Button type="button" variant="outline" onclick={handleCancel} disabled={loading}>
					Cancel
				</Button>
				<Button type="submit" disabled={loading || !name.trim()}>
					{loading ? 'Saving...' : submitButtonText}
				</Button>
			</Dialog.Footer>
		</form>
	</Dialog.Content>
</Dialog.Root>
