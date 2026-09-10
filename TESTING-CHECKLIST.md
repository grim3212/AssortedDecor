# AssortedDecor — in-world testing checklist

Manual checks only. Anything automatable lives in `common/src/gametest/java/.../gametest/`
and runs with `./gradlew :neoforge:runGameTestServer` / `:fabric:runGameTest`.

Run each list on **both** NeoForge and Fabric.

## Colorizers
- [ ] Colorizer brush tooltip names the block it holds
- [ ] Colorizer item animates in the inventory and shows the stored block
- [ ] Breaking a colorizer plays the stored block's particles and sound
- [ ] The fireplace's inside is lit, not black

## Lighting and roads
- [ ] Roadway, sidewalk and stone path textures line up between neighbours

## Signs, clocks, cages
- [ ] Neon sign (standing and wall) opens its edit screen, with a visible background panel
- [ ] Neon sign text renders, glows, and can be recoloured
- [ ] Wall clock shows the time and updates
- [ ] Calendar display updates as the day passes
- [ ] Cage spins the mob it holds
- [ ] Fountain animates

## Entities and misc
- [ ] Wood frame and iron frame hold an item
- [ ] Glass, quartz and steel doors are not solid-shaded (redstone only, no hand click)
