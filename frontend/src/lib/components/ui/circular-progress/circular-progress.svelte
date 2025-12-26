<script lang="ts">
	import { cn } from '$lib/utils';

	interface Props {
		class?: string;
		progress?: number;
		size?: number;
		strokeWidth?: number;
	}

	let { class: className, progress = 0, size = 120, strokeWidth = 8 }: Props = $props();

	let radius = $derived((size - strokeWidth) / 2);
	let circumference = $derived(2 * Math.PI * radius);
	let offset = $derived(circumference - (progress / 100) * circumference);
</script>

<div class={cn('relative inline-flex items-center justify-center', className)}>
	<svg width={size} height={size} class="-rotate-90">
		<!-- Background circle -->
		<circle
			cx={size / 2}
			cy={size / 2}
			r={radius}
			fill="none"
			stroke="currentColor"
			stroke-width={strokeWidth}
			class="text-muted opacity-20"
		/>
		<!-- Progress circle -->
		<circle
			cx={size / 2}
			cy={size / 2}
			r={radius}
			fill="none"
			stroke="currentColor"
			stroke-width={strokeWidth}
			stroke-dasharray={circumference}
			stroke-dashoffset={offset}
			stroke-linecap="round"
			class="text-primary transition-[stroke-dashoffset] duration-1000 ease-out"
		/>
	</svg>
	<div class="absolute inset-0 flex items-center justify-center">
		<span class="text-2xl font-bold text-foreground">
			{Math.round(progress)}%
		</span>
	</div>
</div>
