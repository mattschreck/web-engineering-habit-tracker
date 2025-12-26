<script lang="ts">
	import { page } from '$app/stores';
	import { Button } from '$lib/components/ui/button';
	import ThemeToggle from '$lib/components/theme-toggle.svelte';
	import { Home, User } from 'lucide-svelte';

	$: currentPath = $page.url.pathname;
</script>

<nav class="relative z-10 border-b border-border bg-background/80 backdrop-blur-sm">
	<div class="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
		<div class="flex h-16 items-center justify-between">
			<!-- Logo/Brand -->
			<a href="/dashboard" class="flex items-center transition-opacity hover:opacity-80">
				<h1 class="text-xl font-bold text-primary">Habit Tracker</h1>
			</a>

			<!-- Navigation Links -->
			<div class="flex items-center gap-2">
				<Button
					variant={currentPath === '/dashboard' ? 'default' : 'ghost'}
					size="sm"
					href="/dashboard"
					class="gap-2"
				>
					<Home class="h-4 w-4" />
					<span class="hidden sm:inline">Dashboard</span>
				</Button>

				<Button
					variant={currentPath === '/account' ? 'default' : 'ghost'}
					size="sm"
					href="/account"
					class="gap-2"
				>
					<User class="h-4 w-4" />
					<span class="hidden sm:inline">Account</span>
				</Button>

				<ThemeToggle />
			</div>
		</div>
	</div>
</nav>
