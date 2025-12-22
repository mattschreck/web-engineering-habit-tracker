<script lang="ts">
	import { Button } from '$lib/components/ui/button';
	import { Input } from '$lib/components/ui/input';
	import { Label } from '$lib/components/ui/label';
	import * as Card from '$lib/components/ui/card';
	import { login, type LoginRequest, ApiError } from '$lib/api/client';
	import { goto } from '$app/navigation';

	let usernameOrEmail = $state('');
	let password = $state('');
	let loading = $state(false);
	let error = $state<string | null>(null);

	async function handleLogin(event?: SubmitEvent) {
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

			// Redirect to dashboard after successful login
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

<!-- Black background container -->
<div class="min-h-screen w-full bg-black flex items-center justify-center p-4">
	<!-- Login Card -->
	<Card.Root class="w-full max-w-md">
		<Card.Header>
			<Card.Title class="text-2xl">Login to your account</Card.Title>
			<Card.Description>Enter your credentials to access your habit tracker</Card.Description>
		</Card.Header>

		<Card.Content>
			<form onsubmit={handleLogin}>
				<div class="flex flex-col gap-6">
					<!-- Username/Email Input -->
					<div class="grid gap-2">
						<Label for="usernameOrEmail">Username or Email</Label>
						<Input
							id="usernameOrEmail"
							type="text"
							placeholder="john.doe or john@example.com"
							bind:value={usernameOrEmail}
							onkeypress={handleKeyPress}
							required
							disabled={loading}
						/>
					</div>

					<!-- Password Input -->
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
							required
							disabled={loading}
						/>
					</div>

					<!-- Error Message -->
					{#if error}
						<div class="text-sm text-red-500 bg-red-50 dark:bg-red-950/20 p-3 rounded-md">
							{error}
						</div>
					{/if}
				</div>
			</form>
		</Card.Content>

		<Card.Footer class="flex flex-col gap-3">
			<Button type="submit" class="w-full" disabled={loading}>
				{loading ? 'Logging in...' : 'Login'}
			</Button>

			<div class="text-center text-sm text-muted-foreground">
				Don't have an account?
				<a href="/register" class="text-primary underline-offset-4 hover:underline">
					Sign up
				</a>
			</div>
		</Card.Footer>
	</Card.Root>
</div>
