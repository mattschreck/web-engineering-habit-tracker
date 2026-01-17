<script lang="ts">
	import { Button } from '$lib/components/ui/button';
	import { Input } from '$lib/components/ui/input';
	import { Label } from '$lib/components/ui/label';
	import * as Card from '$lib/components/ui/card';
	import { register, type RegisterRequest, ApiError } from '$lib/api/client';
	import { goto } from '$app/navigation';
	import ThemeToggle from '$lib/components/theme-toggle.svelte';
	import { BackgroundBeams } from '$lib/components/ui/background-beams';

	let username = $state('');
	let email = $state('');
	let password = $state('');
	let confirmPassword = $state('');
	let loading = $state(false);
	let error = $state<string | null>(null);

	async function handleRegister(event?: Event) {
		event?.preventDefault();
		error = null;

		if (password !== confirmPassword) {
			error = 'Passwords do not match';
			return;
		}

		if (password.length < 8) {
			error = 'Password must be at least 8 characters long';
			return;
		}

		loading = true;

		try {
			const registerData: RegisterRequest = {
				username,
				email,
				password
			};

			const response = await register(registerData);
			console.log('Registration successful:', response.message);

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
			handleRegister();
		}
	}
</script>

<svelte:head>
	<title>Register - Habit Tracker</title>
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
					<Button variant="outline" href="/login">Login</Button>
					<ThemeToggle />
				</div>
			</div>
		</div>
	</nav>

	<div class="relative z-10 flex min-h-[calc(100vh-4rem)] w-full items-center justify-center p-4">
		<Card.Root class="-my-4 w-full max-w-sm">
			<Card.Header>
				<Card.Title>Create an account</Card.Title>
				<Card.Description>Enter your information to get started</Card.Description>
				<Card.Action>
					<Button variant="link" href="/login">Login</Button>
				</Card.Action>
			</Card.Header>

			<Card.Content>
				<form onsubmit={handleRegister}>
					<div class="flex flex-col gap-4">
						<div class="grid gap-2">
							<Label for="username">Username</Label>
							<Input
								id="username"
								type="text"
								placeholder="john_doe"
								bind:value={username}
								onkeypress={handleKeyPress}
								autocomplete="username"
								required
								disabled={loading}
							/>
						</div>

						<div class="grid gap-2">
							<Label for="email">Email</Label>
							<Input
								id="email"
								type="email"
								placeholder="john@example.com"
								bind:value={email}
								onkeypress={handleKeyPress}
								autocomplete="email"
								required
								disabled={loading}
							/>
						</div>

						<div class="grid gap-2">
							<Label for="password">Password</Label>
							<Input
								id="password"
								type="password"
								placeholder="Minimum 8 characters"
								bind:value={password}
								onkeypress={handleKeyPress}
								autocomplete="new-password"
								required
								disabled={loading}
							/>
						</div>

						<div class="grid gap-2">
							<Label for="confirmPassword">Confirm Password</Label>
							<Input
								id="confirmPassword"
								type="password"
								placeholder="Re-enter your password"
								bind:value={confirmPassword}
								onkeypress={handleKeyPress}
								autocomplete="new-password"
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
				<Button type="button" onclick={handleRegister} class="w-full" disabled={loading}>
					{loading ? 'Creating account...' : 'Create account'}
				</Button>
			</Card.Footer>
		</Card.Root>
	</div>
</div>
