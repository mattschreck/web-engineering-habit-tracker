<script lang="ts">
	import { onMount } from 'svelte';
	import { goto } from '$app/navigation';
	import { isAuthenticated } from '$lib/api/client';
	import { Button } from '$lib/components/ui/button/index.js';
	import ThemeToggle from '$lib/components/theme-toggle.svelte';
	import { BackgroundBeams } from '$lib/components/ui/background-beams';

	// Auto-redirect to dashboard if already logged in
	onMount(() => {
		if (isAuthenticated()) {
			goto('/dashboard');
		}
	});
</script>

<div class="relative min-h-screen overflow-hidden bg-background">
	<BackgroundBeams />

	<!-- Navigation -->
	<nav class="relative z-10 border-b border-border">
		<div class="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
			<div class="flex h-16 items-center justify-between">
				<div class="flex items-center">
					<h1 class="text-2xl font-bold text-primary">Habit Tracker</h1>
				</div>
				<div class="flex items-center gap-4">
					<Button variant="outline" href="/login">Login</Button>
					<Button href="/register">Register</Button>
					<ThemeToggle />
				</div>
			</div>
		</div>
	</nav>

	<!-- Hero Section -->
	<main class="relative z-10 mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
		<div class="flex min-h-[calc(100vh-4rem)] flex-col items-center justify-center text-center">
			<h2 class="mb-6 text-5xl font-bold text-foreground md:text-6xl lg:text-7xl">
				Build Better <span class="text-primary">Habits</span>
			</h2>
			<p class="mb-8 max-w-2xl text-lg text-muted-foreground md:text-xl">
				Track your daily habits, visualize your progress, and achieve your goals. Start building a
				better version of yourself today.
			</p>
			<div class="flex gap-4">
				<Button size="lg" href="/register">Get Started</Button>
				<Button size="lg" variant="outline" href="/login">Sign In</Button>
			</div>
		</div>
	</main>
</div>
