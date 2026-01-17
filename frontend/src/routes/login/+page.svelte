<script lang="ts">
	import { Button } from '$lib/components/ui/button';
	import { Input } from '$lib/components/ui/input';
	import { Label } from '$lib/components/ui/label';
	import * as Card from '$lib/components/ui/card';
	import { login, type LoginRequest, ApiError } from '$lib/api/client';
	import { goto } from '$app/navigation';
	import ThemeToggle from '$lib/components/theme-toggle.svelte';
	import { BackgroundBeams } from '$lib/components/ui/background-beams';

	let usernameOrEmail = $state('');
	let password = $state('');
	let loading = $state(false);
	let error = $state<string | null>(null);

	async function handleLogin(event?: Event) {
		event?.preventDefault();
		error = null;
		loading = true;

		try {
			const loginData: LoginRequest = {
				usernameOrEmail,
				password
			};

			const response = await login(loginData);
			console.log('Login successful:', response.message);

			goto('/dashboard');
		} catch (err) {
			if (err instanceof ApiError) {
				error = err.error;
				if (err.details?.validationErrors) {
					const validationErrors = Object.values(err.details.validationErrors).join(', ');
					error = `${error}: ${validationErrors}`;
				}
			} else {
				error = 'An unexpected error occurred';
			}
		} finally {
			loading = false;
		}
	}

	function handleKeyPress(event: KeyboardEvent) {
		if (event.key === 'Enter' && !loading) {
			handleLogin();
		}
	}
</script>

<svelte:head>
	<title>Login - Habit Tracker</title>
</svelte:head>

<div class="relative min-h-screen overflow-hidden bg-background">
	<BackgroundBeams />

	<nav class="relative z-10 border-b border-border">
		<div class="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
			<div class="flex h-16 items-center justify-between">
				<a href="/" class="flex items-center">
					<h1 class="text-2xl font-bold text-primary transition-opacity hover:opacity-80">
						Habit Tracker
					</h1>
				</a>
				<div class="flex items-center gap-4">
					<Button variant="outline" href="/register">Register</Button>
					<ThemeToggle />
				</div>
			</div>
		</div>
	</nav>

	<div class="relative z-10 flex min-h-[calc(100vh-4rem)] w-full items-center justify-center p-4">
		<Card.Root class="-my-4 w-full max-w-sm">
			<Card.Header>
				<Card.Title>Login to your account</Card.Title>
				<Card.Description>Enter your credentials to access your habit tracker</Card.Description>
				<Card.Action>
					<Button variant="link" href="/register">Sign Up</Button>
				</Card.Action>
			</Card.Header>

			<Card.Content>
				<form onsubmit={handleLogin}>
					<div class="flex flex-col gap-6">
						<div class="grid gap-2">
							<Label for="usernameOrEmail">Username or Email</Label>
							<Input
								id="usernameOrEmail"
								type="text"
								placeholder="john.doe or john@example.com"
								bind:value={usernameOrEmail}
								onkeypress={handleKeyPress}
								autocomplete="username"
								required
								disabled={loading}
							/>
						</div>

						<div class="grid gap-2">
							<div class="flex items-center justify-between">
								<Label for="password">Password</Label>
							</div>
							<Input
								id="password"
								type="password"
								placeholder="Enter your password"
								bind:value={password}
								onkeypress={handleKeyPress}
								autocomplete="current-password"
								required
								disabled={loading}
							/>
						</div>

						{#if error}
							<div class="rounded-md bg-red-50 p-3 text-sm text-red-500 dark:bg-red-950/20">
								{error}
							</div>
						{/if}
					</div>
				</form>
			</Card.Content>

			<Card.Footer class="flex-col gap-2">
				<Button type="button" onclick={handleLogin} class="w-full" disabled={loading}>
					{loading ? 'Logging in...' : 'Login'}
				</Button>
			</Card.Footer>
		</Card.Root>
	</div>
</div>
