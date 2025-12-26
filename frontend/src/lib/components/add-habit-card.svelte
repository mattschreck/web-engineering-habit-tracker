<script lang="ts">
	import * as Card from '$lib/components/ui/card';
	import { Plus } from 'lucide-svelte';

	interface Props {
		onClick?: () => void;
	}

	let { onClick }: Props = $props();
</script>

<button
	onclick={onClick}
	class="group w-full transition-all hover:scale-105 focus:ring-2 focus:ring-primary focus:ring-offset-2 focus:outline-none"
>
	<Card.Root
		class="h-full cursor-pointer border-2 border-dashed hover:border-primary hover:bg-accent/50"
	>
		<Card.Content class="flex min-h-[300px] items-center justify-center p-6">
			<div class="flex flex-col items-center gap-3">
				<div
					class="flex h-24 w-24 items-center justify-center rounded-full border-2 border-dashed border-muted-foreground/30 group-hover:border-primary"
				>
					<Plus class="h-12 w-12 text-muted-foreground group-hover:text-primary" />
				</div>
				<p class="text-sm font-medium text-muted-foreground group-hover:text-primary">
					Add New Habit
				</p>
			</div>
		</Card.Content>
	</Card.Root>
</button>
