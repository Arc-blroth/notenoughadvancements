package ai.arcblroth.stutils.notenoughadvancements.mixin;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.PlayerAdvancementTracker;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;
import java.util.Set;

@Mixin(PlayerAdvancementTracker.class)
public class MixinPlayerAdvancementTracker {
	@Shadow @Final
	private Map<Advancement, AdvancementProgress> advancementToProgress;

	@Shadow @Final
	private Set<Advancement> visibleAdvancements;

	@Shadow @Final
	private Set<Advancement> visibilityUpdates;

	@Shadow @Final
	private Set<Advancement> progressUpdates;

	@Shadow
	private boolean canSee(Advancement advancement) { return false; }

	/**
	 * @reason literally the whole point
	 * @author Arc'blroth
	 */
	@Overwrite
	private void updateDisplay(Advancement root) {
		ObjectArrayList<Advancement> stack = new ObjectArrayList<>();
		stack.add(root);
		while (!stack.isEmpty()) {
			Advancement advancement = stack.pop();

			boolean canSee = this.canSee(advancement);
			boolean visible = this.visibleAdvancements.contains(advancement);
			if (canSee && !visible) {
				this.visibleAdvancements.add(advancement);
				this.visibilityUpdates.add(advancement);
				if (this.advancementToProgress.containsKey(advancement)) {
					this.progressUpdates.add(advancement);
				}
			} else if (!canSee && visible) {
				this.visibleAdvancements.remove(advancement);
				this.visibilityUpdates.add(advancement);
			}

			if (canSee != visible && advancement.getParent() != null) {
				stack.push(advancement.getParent());
			}

			for (Advancement child : advancement.getChildren()) {
				stack.push(child);
			}
		}
	}
}
