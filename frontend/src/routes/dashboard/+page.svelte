<script lang="ts">
	import { onMount } from 'svelte';
	import { goto } from '$app/navigation';
	import { isAuthenticated, logout, getCurrentUser, type UserResponse } from '$lib/api/client';

	let user = $state<UserResponse | null>(null);
	let loading = $state(true);

	onMount(async () => {
		// Check if user is authenticated
		if (!isAuthenticated()) {
			goto('/login');
			return;
		}

		// Fetch current user data
		try {
			user = await getCurrentUser();
		} catch (error) {
			console.error('Failed to fetch user:', error);
			// If token is invalid, redirect to login
			logout();
			goto('/login');
		} finally {
			loading = false;
		}
	});

	function handleLogout() {
		logout();
		goto('/login');
	}
</script>

<svelte:head>
	<title>Dashboard - Habit Tracker</title>
</svelte:head>

<div class="min-h-screen bg-background">
	{#if loading}
		<div class="flex items-center justify-center min-h-screen">
			<div class="text-lg">Loading...</div>
		</div>
	{:else if user}
		<!-- Header -->
		<header class="border-b">
			<div class="container mx-auto px-4 py-4 flex items-center justify-between">
				<h1 class="text-2xl font-bold">Habit Tracker</h1>
				<div class="flex items-center gap-4">
					<span class="text-sm text-muted-foreground">Welcome, {user.username}!</span>
					<button
						onclick={handleLogout}
						class="text-sm text-primary hover:underline underline-offset-4"
					>
						Logout
					</button>
				</div>
			</div>
		</header>

		<!-- Main Content -->
		<main class="container mx-auto px-4 py-8">
			<div class="space-y-6">
				<div>
					<h2 class="text-3xl font-bold">Dashboard</h2>
					<p class="text-muted-foreground mt-2">Welcome to your habit tracking dashboard!</p>
				</div>

				<!-- User Info Card -->
				<div class="border rounded-lg p-6 space-y-2">
					<h3 class="font-semibold text-lg">Your Profile</h3>
					<div class="grid gap-2 text-sm">
						<div class="flex justify-between">
							<span class="text-muted-foreground">Username:</span>
							<span class="font-medium">{user.username}</span>
						</div>
						<div class="flex justify-between">
							<span class="text-muted-foreground">Email:</span>
							<span class="font-medium">{user.email}</span>
						</div>
						<div class="flex justify-between">
							<span class="text-muted-foreground">Account Status:</span>
							<span class="font-medium">{user.enabled ? 'Active' : 'Inactive'}</span>
						</div>
						<div class="flex justify-between">
							<span class="text-muted-foreground">Member Since:</span>
							<span class="font-medium">{new Date(user.createdAt).toLocaleDateString()}</span>
						</div>
					</div>
				</div>

				<!-- Placeholder for future habit content -->
				<div class="border border-dashed rounded-lg p-12 text-center">
					<h3 class="text-xl font-semibold mb-2">Your Habits</h3>
					<p class="text-muted-foreground">Habit tracking functionality coming soon!</p>
				</div>
			</div>
		</main>
	{/if}
</div>
