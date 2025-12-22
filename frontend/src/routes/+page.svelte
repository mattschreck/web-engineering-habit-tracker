<script lang="ts">
	import { onMount } from 'svelte';
	import { goto } from '$app/navigation';
	import { isAuthenticated } from '$lib/api/client';

	onMount(() => {
		// Redirect based on authentication status
		if (isAuthenticated()) {
			goto('/dashboard');
		} else {
			goto('/login');
		}
	});
</script>

<!-- Loading state while redirecting -->
<div class="flex min-h-screen items-center justify-center bg-black">
	<div class="text-lg text-white">Redirecting...</div>
</div>
