<script lang="ts">
	import { Button } from '$lib/components/ui/button';
	import { Input } from '$lib/components/ui/input';
	import { Label } from '$lib/components/ui/label';
	import * as Card from '$lib/components/ui/card';
	import { register, type RegisterRequest, ApiError } from '$lib/api/client';
	import { goto } from '$app/navigation';

	let username = $state('');
	let email = $state('');
	let password = $state('');
	let confirmPassword = $state('');
	let loading = $state(false);
	let error = $state<string | null>(null);

	async function handleRegister(event?: SubmitEvent) {
		event?.preventDefault();
		error = null;

		// Validate passwords match
		if (password !== confirmPassword) {
			error = 'Passwords do not match';
			return;
		}

		// Validate password length
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

			// Redirect to dashboard after successful registration
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

<!-- Black background container -->
<div class="flex min-h-screen w-full items-center justify-center bg-black p-4">
	<!-- Register Card -->
	<Card.Root class="w-full max-w-md">
		<Card.Header>
			<Card.Title class="text-2xl">Create an account</Card.Title>
			<Card.Description>Enter your information to get started</Card.Description>
		</Card.Header>

		<Card.Content>
			<form onsubmit={handleRegister}>
				<div class="flex flex-col gap-4">
					<!-- Username Input -->
					<div class="grid gap-2">
						<Label for="username">Username</Label>
						<Input
							id="username"
							type="text"
							placeholder="john_doe"
							bind:value={username}
							onkeypress={handleKeyPress}
							required
							disabled={loading}
						/>
					</div>

					<!-- Email Input -->
					<div class="grid gap-2">
						<Label for="email">Email</Label>
						<Input
							id="email"
							type="email"
							placeholder="john@example.com"
							bind:value={email}
							onkeypress={handleKeyPress}
							required
							disabled={loading}
						/>
					</div>

					<!-- Password Input -->
					<div class="grid gap-2">
						<Label for="password">Password</Label>
						<Input
							id="password"
							type="password"
							placeholder="Minimum 8 characters"
							bind:value={password}
							onkeypress={handleKeyPress}
							required
							disabled={loading}
						/>
					</div>

					<!-- Confirm Password Input -->
					<div class="grid gap-2">
						<Label for="confirmPassword">Confirm Password</Label>
						<Input
							id="confirmPassword"
							type="password"
							placeholder="Re-enter your password"
							bind:value={confirmPassword}
							onkeypress={handleKeyPress}
							required
							disabled={loading}
						/>
					</div>

					<!-- Error Message -->
					{#if error}
						<div class="rounded-md bg-red-50 p-3 text-sm text-red-500 dark:bg-red-950/20">
							{error}
						</div>
					{/if}
				</div>
			</form>
		</Card.Content>

		<Card.Footer class="flex flex-col gap-3">
			<Button type="submit" class="w-full" disabled={loading}>
				{loading ? 'Creating account...' : 'Create account'}
			</Button>

			<div class="text-center text-sm text-muted-foreground">
				Already have an account?
				<a href="/login" class="text-primary underline-offset-4 hover:underline"> Login </a>
			</div>
		</Card.Footer>
	</Card.Root>
</div>
