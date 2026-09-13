# AssortedDecor — in-world testing checklist

Manual checks only. Anything automatable lives in `common/src/gametest/java/.../gametest/`
and runs with `./gradlew :neoforge:runGameTestServer` / `:fabric:runGameTest`.

Run each list on **both** NeoForge and Fabric.

## Colorizers
- [ ] Colorizer brush tooltip names the block it holds
- [ ] Colorizer item animates in the inventory and shows the stored block
- [ ] Breaking a colorizer plays the stored block's particles and sound
- [ ] The fireplace's inside is lit, not black
- [ ] A placed colorizer shows its stored block
- [ ] A wall-mounted colorizer table with two neighbours looks right
- [ ] On a dedicated server, a colorizer painted by one player updates for a second player, and
      one painted with glowstone lights the room for them too
- [ ] A colorizer holding stone darkens the room behind it and casts a shadow on the client too; a
      stone-filled colorizer stairs or fence does not. The gametests only watch the server's light
      engine, and the client sees the `light_dampening` state through a block update

## Lighting and roads
- [ ] Sidewalk sprinting feels faster than the roadway beside it. That it *is* faster is the
      `sidewalk_is_the_faster_surface` gametest; what a hand check adds is whether 1.35 is the
      right amount
- [ ] Fences, lanterns and tubes draw cutout

## Signs, clocks, cages
- [ ] Neon sign (standing and wall) opens its edit screen, with a visible background panel
- [ ] Neon sign text renders, glows, and can be recoloured
- [ ] Wall clock shows the time and updates
- [ ] Calendar display updates as the day passes
- [ ] Cage spins the mob it holds
- [ ] Fountain has water particles

## Entities and misc
- [ ] Wood frame and iron frame can be changed and dyed
- [ ] Frames save their design and dye across loads
- [ ] Glass, quartz and steel doors are not solid-shaded (redstone only, no hand click)

## Rendering
- [ ] Nothing a renderer draws is subtly off. Sprite UVs now take 0-1 and ARGB colours no longer
      force alpha, so a mistake shows as the wrong texture region or a missing tint, not a crash
