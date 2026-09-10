# AssortedDecor — in-world testing checklist

Manual checks only. Anything automatable lives in `common/src/main/java/.../gametest/`
and runs with `./gradlew :neoforge:runGameTestServer` / `:fabric:runGameTest`.

Run each list on **both** NeoForge and Fabric.

## Colorizers
- [ ] Colorizer brush picks up a block's texture, and the tooltip names it
- [ ] Using the brush on a colorizer applies that texture
- [ ] Every colorizer shape takes the texture: slab, vertical slab, stairs, wall, fence, fence gate,
      door, trapdoor, slope, corner, pyramid, table, chair, stool, counter, chimney, lamp post
- [ ] Colorizer item animates in the inventory and shows the stored block
- [ ] Breaking a colorizer plays the stored block's particles and sound, and does not crash
- [ ] Firepit, fireplace, firering and stove light up, and the fireplace's inside is lit not black
- [ ] Colorizer keeps its texture across a world reload

## Painting and colour
- [ ] Each paint roller recolours a wool/terracotta/concrete block to its colour
- [ ] Paint roller dyes a sheep
- [ ] Siding (horizontal and vertical) keeps its colour when placed from the item
- [ ] All 16 fluro blocks glow

## Lighting and roads
- [ ] Bone, iron and paper lanterns place on floor and ceiling
- [ ] Illumination plate and illumination tube emit light
- [ ] Roadway, white roadway, roadway light, manhole, sidewalk and stone path all place and connect

## Signs, clocks, cages
- [ ] Neon sign (standing and wall) opens its edit screen, with a visible background panel
- [ ] Neon sign text renders, glows, and can be recoloured
- [ ] Wall clock shows the time and updates
- [ ] Calendar shows the date and updates
- [ ] Cage holds a mob, spins it, and drops its contents when broken
- [ ] Planter pot holds a plant; fountain animates

## Entities and misc
- [ ] Wood frame and iron frame place on a wall, hold an item, and drop it when broken
- [ ] Wallpaper places on a wall and takes a colour
- [ ] Chain link fence and chain link door connect and open
- [ ] Glass, quartz and steel doors open and are not solid-shaded
- [ ] Asphalt, decorative stone, clay decoration and bone decoration place normally

## Creative
- [ ] The Assorted Decor tab exists and every block/item in it has a model and a name
