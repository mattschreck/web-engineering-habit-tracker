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
<div class="min-h-screen bg-black flex items-center justify-center">
	<div class="text-white text-lg">Redirecting...</div>
</div>
