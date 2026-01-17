<script lang="ts">
	import { onMount } from 'svelte';
	import { goto } from '$app/navigation';
	import {
		isAuthenticated,
		getCurrentUserFromStorage,
		logout,
		deleteCurrentUser,
		updateCurrentUser
	} from '$lib/api/client';
	import type { UserResponse, UpdateUserRequest } from '$lib/api/client';
	import Navigation from '$lib/components/navigation.svelte';
	import { BackgroundBeams } from '$lib/components/ui/background-beams';
	import * as Card from '$lib/components/ui/card';
	import * as Dialog from '$lib/components/ui/dialog';
	import { Button } from '$lib/components/ui/button';
	import { Input } from '$lib/components/ui/input';
	import { Label } from '$lib/components/ui/label';
	import { User, Mail, Calendar, Shield, Trash2, Pencil, X, Check } from 'lucide-svelte';

	let user = $state<UserResponse | null>(null);
	let showDeleteDialog = $state(false);
	let isDeleting = $state(false);
	let deleteError = $state<string | null>(null);

	let isEditing = $state(false);
	let isSaving = $state(false);
	let editError = $state<string | null>(null);
	let editUsername = $state('');

	onMount(() => {
		if (!isAuthenticated()) {
			goto('/login');
			return;
		}
		user = getCurrentUserFromStorage();
		if (user) {
			editUsername = user.username;
		}
	});

	function handleLogout() {
		logout();
		goto('/');
	}

	function startEditing() {
		if (user) {
			editUsername = user.username;
		}
		editError = null;
		isEditing = true;
	}

	function cancelEditing() {
		if (user) {
			editUsername = user.username;
		}
		editError = null;
		isEditing = false;
	}

	async function handleSaveProfile() {
		if (!user) return;

		if (!editUsername.trim()) {
			editError = 'Username is required';
			return;
		}
		if (editUsername.trim().length < 3) {
			editError = 'Username must be at least 3 characters';
			return;
		}

		if (editUsername.trim() === user.username) {
			isEditing = false;
			return;
		}

		isSaving = true;
		editError = null;

		try {
			const updateData: UpdateUserRequest = {
				username: editUsername.trim()
			};

			const updatedUser = await updateCurrentUser(updateData);
			user = updatedUser;
			isEditing = false;
		} catch (error) {
			if (error instanceof Error) {
				editError = error.message;
			} else {
				editError = 'Failed to update profile';
			}
		} finally {
			isSaving = false;
		}
	}

	async function handleDeleteAccount() {
		isDeleting = true;
		deleteError = null;

		try {
			await deleteCurrentUser();
			goto('/');
		} catch (error) {
			deleteError = error instanceof Error ? error.message : 'Failed to delete account';
			isDeleting = false;
		}
	}
</script>

<svelte:head>
	<title>Account - Habit Tracker</title>
</svelte:head>

<div class="relative min-h-screen overflow-hidden bg-background">
	<BackgroundBeams />

	<div class="relative z-10 min-h-screen">
		<Navigation />

		<main class="mx-auto max-w-2xl px-4 py-8 sm:px-6 lg:px-8">
			<div class="mb-8">
				<h1 class="text-3xl font-bold text-foreground">Account</h1>
				<p class="mt-2 text-muted-foreground">
					{isEditing ? 'Edit your account information' : 'View your account information'}
				</p>
			</div>

			{#if user}
				<Card.Root>
					<Card.Header>
						<div class="flex items-center justify-between">
							<div>
								<Card.Title>Profile Information</Card.Title>
								<Card.Description>
									{isEditing ? 'Update your username' : 'Your account details'}
								</Card.Description>
							</div>
							{#if !isEditing}
								<Button variant="outline" size="sm" onclick={startEditing}>
									<Pencil class="mr-2 h-4 w-4" />
									Edit
								</Button>
							{/if}
						</div>
					</Card.Header>

					<Card.Content class="space-y-4">
						{#if editError}
							<div class="rounded-md bg-destructive/10 p-3 text-sm text-destructive">
								{editError}
							</div>
						{/if}

						<div class="flex items-center gap-4">
							<div class="flex h-10 w-10 items-center justify-center rounded-full bg-primary/10">
								<User class="h-5 w-5 text-primary" />
							</div>
							<div class="flex-1">
								<Label class="text-sm font-medium text-muted-foreground">Username</Label>
								{#if isEditing}
									<Input
										bind:value={editUsername}
										placeholder="Enter username"
										class="mt-1"
										autocomplete="username"
									/>
								{:else}
									<p class="text-base font-semibold">{user.username}</p>
								{/if}
							</div>
						</div>

						<div class="flex items-center gap-4">
							<div class="flex h-10 w-10 items-center justify-center rounded-full bg-primary/10">
								<Mail class="h-5 w-5 text-primary" />
							</div>
							<div class="flex-1">
								<p class="text-sm font-medium text-muted-foreground">Email</p>
								<p class="text-base font-semibold">{user.email}</p>
							</div>
						</div>

						<div class="flex items-center gap-4">
							<div class="flex h-10 w-10 items-center justify-center rounded-full bg-primary/10">
								<Calendar class="h-5 w-5 text-primary" />
							</div>
							<div class="flex-1">
								<p class="text-sm font-medium text-muted-foreground">Member Since</p>
								<p class="text-base font-semibold">
									{new Date(user.createdAt).toLocaleDateString('en-US', {
										year: 'numeric',
										month: 'long',
										day: 'numeric'
									})}
								</p>
							</div>
						</div>

						<div class="flex items-center gap-4">
							<div class="flex h-10 w-10 items-center justify-center rounded-full bg-primary/10">
								<Shield class="h-5 w-5 text-primary" />
							</div>
							<div class="flex-1">
								<p class="text-sm font-medium text-muted-foreground">Account Status</p>
								<p class="text-base font-semibold">
									{user.enabled ? 'Active' : 'Inactive'}
								</p>
							</div>
						</div>
					</Card.Content>

					<Card.Footer class="flex justify-between gap-2">
						{#if isEditing}
							<Button variant="outline" onclick={cancelEditing} disabled={isSaving}>
								<X class="mr-2 h-4 w-4" />
								Cancel
							</Button>
							<Button onclick={handleSaveProfile} disabled={isSaving}>
								<Check class="mr-2 h-4 w-4" />
								{isSaving ? 'Saving...' : 'Save Changes'}
							</Button>
						{:else}
							<Button variant="destructive" onclick={() => (showDeleteDialog = true)}>
								<Trash2 class="mr-2 h-4 w-4" />
								Delete Account
							</Button>
							<Button variant="outline" onclick={handleLogout}>Logout</Button>
						{/if}
					</Card.Footer>
				</Card.Root>
			{/if}

			<Dialog.Root bind:open={showDeleteDialog}>
				<Dialog.Content class="sm:max-w-md">
					<Dialog.Header>
						<Dialog.Title>Delete Account</Dialog.Title>
						<Dialog.Description>
							Are you absolutely sure you want to delete your account? This action cannot be undone
							and will permanently delete all your data, including habits and logs.
						</Dialog.Description>
					</Dialog.Header>

					{#if deleteError}
						<div class="rounded-md bg-destructive/10 p-3 text-sm text-destructive">
							{deleteError}
						</div>
					{/if}

					<Dialog.Footer class="flex-col gap-2 sm:flex-row">
						<Button
							variant="outline"
							onclick={() => (showDeleteDialog = false)}
							disabled={isDeleting}
						>
							Cancel
						</Button>
						<Button
							variant="destructive"
							onclick={handleDeleteAccount}
							disabled={isDeleting}
							class="w-full sm:w-auto"
						>
							{isDeleting ? 'Deleting...' : 'Yes, Delete My Account'}
						</Button>
					</Dialog.Footer>
				</Dialog.Content>
			</Dialog.Root>
		</main>
	</div>
</div>
