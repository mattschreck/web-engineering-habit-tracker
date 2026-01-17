<script lang="ts">
	import type { HabitResponse, HabitRequest, HabitFrequency } from '$lib/api/client';
	import { createHabit, updateHabit, ApiError } from '$lib/api/client';
	import * as Dialog from '$lib/components/ui/dialog';
	import { Button } from '$lib/components/ui/button';
	import { Input } from '$lib/components/ui/input';
	import { Label } from '$lib/components/ui/label';

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
	let hasEndDate = $state(false);
	let loading = $state(false);
	let error = $state<string | null>(null);

	let isEdit = $derived(habit !== null);
	let dialogTitle = $derived(isEdit ? 'Habit bearbeiten' : 'Neues Habit');
	let submitButtonText = $derived(isEdit ? 'Aktualisieren' : 'Erstellen');

	function getTodayDate(): string {
		const today = new Date();
		return today.toISOString().split('T')[0];
	}

	$effect(() => {
		if (open) {
			if (habit) {
				name = habit.name;
				description = habit.description || '';
				frequency = habit.frequency;
				startDate = habit.startDate || '';
				endDate = habit.endDate || '';
				hasEndDate = !!habit.endDate;
			} else {
				name = '';
				description = '';
				frequency = 'DAILY';
				startDate = getTodayDate();
				endDate = '';
				hasEndDate = false;
			}
			error = null;
		}
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
				endDate: hasEndDate ? (endDate || null) : null,
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
				error = 'Ein unerwarteter Fehler ist aufgetreten';
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
	<Dialog.Content class="sm:max-w-106.25">
		<Dialog.Header>
			<Dialog.Title>{dialogTitle}</Dialog.Title>
			<Dialog.Description>
				{isEdit ? 'Habit-Details anpassen.' : 'Neues Habit zum Verfolgen erstellen.'}
			</Dialog.Description>
		</Dialog.Header>

		<form onsubmit={handleSubmit} class="space-y-4">
			<div class="grid gap-2">
				<Label for="name">Name *</Label>
				<Input
					id="name"
					name="habit-name"
					type="text"
					placeholder="z.B. Morgen-Workout"
					bind:value={name}
					autocomplete="off"
					required
					disabled={loading}
					maxlength={100}
				/>
			</div>

			<div class="grid gap-2">
				<Label for="description">Beschreibung</Label>
				<Input
					id="description"
					name="habit-description"
					type="text"
					placeholder="Optional"
					bind:value={description}
					autocomplete="off"
					disabled={loading}
					maxlength={500}
				/>
			</div>

			<div class="grid gap-2">
				<Label for="frequency">Frequenz *</Label>
				<select
					id="frequency"
					bind:value={frequency}
					disabled={loading}
					required
					class="flex h-9 w-full rounded-md border border-input bg-background px-3 py-1 text-sm shadow-xs outline-none transition-colors disabled:cursor-not-allowed disabled:opacity-50 focus-visible:border-ring focus-visible:ring-[3px] focus-visible:ring-ring/50"
				>
					<option value="DAILY">Täglich</option>
					<option value="WEEKLY">Wöchentlich</option>
					<option value="MONTHLY">Monatlich</option>
				</select>
			</div>

			<div class="grid gap-2">
				<Label for="startDate">Startdatum *</Label>
				<Input
					id="startDate"
					type="date"
					bind:value={startDate}
					autocomplete="off"
					disabled={loading}
					min={getTodayDate()}
					required
				/>
			</div>

			<div class="grid gap-2">
				<div class="flex items-center gap-2">
					<input
						id="hasEndDate"
						type="checkbox"
						bind:checked={hasEndDate}
						disabled={loading}
						class="h-4 w-4 rounded border-input"
					/>
					<Label for="hasEndDate" class="cursor-pointer font-normal">
						Enddatum setzen
					</Label>
				</div>
			</div>

			{#if hasEndDate}
				<div class="grid gap-2">
					<Label for="endDate">Enddatum *</Label>
					<Input
						id="endDate"
						type="date"
						bind:value={endDate}
						autocomplete="off"
						disabled={loading}
						min={startDate || undefined}
						required
					/>
				</div>
			{/if}

			{#if error}
				<div class="rounded-md bg-red-50 p-3 text-sm text-red-500 dark:bg-red-950/20">
					{error}
				</div>
			{/if}

			<Dialog.Footer>
				<Button type="button" variant="outline" onclick={handleCancel} disabled={loading}>
					Abbrechen
				</Button>
				<Button type="submit" disabled={loading || !name.trim()}>
					{loading ? 'Speichern...' : submitButtonText}
				</Button>
			</Dialog.Footer>
		</form>
	</Dialog.Content>
</Dialog.Root>
