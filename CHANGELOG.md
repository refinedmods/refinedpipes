# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [0.6.2] - 2022-06-04

### Changed

- Ported to Minecraft 1.18.2.

## [0.6.1] - 2022-01-30

### Added

- Added German translation by [@Wizqok](https://github.com/Wizqok).
- Added Korean translation by [@mindy15963](https://github.com/mindy15963).

### Fixed

- Fixed random client crashes by [@malte0811](https://github.com/malte0811).
- Fixed crash when Extractor Attachment is interacting with inventories that have no slots.

## [0.6.0] - 2021-12-18

### Added

- Ported to Minecraft 1.18.1.

## [0.5.2] - 2022-01-30

### Fixed

- Fixed random client crashes by [@malte0811](https://github.com/malte0811).
- Fixed crash when Extractor Attachment is interacting with inventories that have no slots.

## [0.5.1] - 2021-11-28

### Added

- Added Italian translation by [@maicol07](https://github.com/maicol07).
- Updated Russian translation by [@KnottyManatee55](https://github.com/KnottyManatee55).
- Updated Spanish translations by [@d-l-n](https://github.com/d-l-n).

### Changed

- Changed textures and models by [@Pierniki](https://github.com/Pierniki).
- Use tags in recipes for glass and slimeballs.

### Fixed

- Fixed crash when breaking fluid pipe.
- Fixed Extractor Attachment failing when it can't extract from the first slot it finds.
- Fixed Extractor Attachment not trying other destinations when an item can't be inserted in a single destination.
- Fixed another pipe rendering crash by [@lone-wolf-akela](https://github.com/lone-wolf-akela).

## [0.5.0] - 2020-11-16

### Added

- Added Russian translation by [@KhottyManatee55](https://github.com/KhottyManatee55).
- Added Chinese translation [@liuseniubi](https://github.com/liuseniubi).

### Changed

- Ported to Minecraft 1.16.

## [0.4.2] - 2020-05-29

### Fixed

- Fixed extremely high memory usage by [@Chocohead](https://github.com/Chocohead).

## [0.4.1] - 2020-05-09

### Added

- Added formatting to quantities in the tooltips.

### Fixed

- Fixed invalid cast server crash.
- Fixed log spamming when destination for items are not found.
- Fixed crash when item pipe in round robin mode no longer has any destination.

## [0.4.0] - 2020-04-10

### Added

- Added filtering options for the Extractor Attachment.
- Added redstone mode options for the Extractor Attachment.
- Added whitelist / blacklist options for the Extractor Attachment.
- Added routing mode (nearest first, furthest first, random and round robin) options for the Extractor Attachment.
- Added exact mode (NBT sensitivity) options for the Extractor Attachment.
- Added stack size configurability to the Extractor Attachment.

### Fixed

- Improved performance of rendering pipes.
- Fixed rendering bug where fluid remained in pipes.

## [0.3.0] - 2020-04-05

Note: Due to fluid networks now being split up by their tier, all fluid pipes from version 0.2.1 and earlier are
incompatible and won't be functioning. This can be fixed by breaking and replacing all the fluid pipes in a network.

### Added

- Implemented pick block on attachments.
- Added energy pipes.
- Added new tiers for the Fluid Pipe: Elite and Ultimate.

### Fixed

- Improved performance of calculating pipe shapes.

## [0.2.1] - 2020-03-30

Note: Due to refactoring of the network architecture, pipes from version 0.2 and earlier are incompatible and won't be
functioning. This can be fixed by breaking and replacing all the pipes in a network.

### Added

- Added ghost hitboxes when holding an attachment in hand.

### Changed

- Refactored network architecture.

## [0.2.0] - 2020-03-29

### Added

- Added fluid pipes.

### Changed

- Improved tooltips.

## [0.1.4] - 2020-03-27

### Added

- Added hitboxes for the attachments on pipes.
- Added a config file.

## [0.1.3] - 2020-03-26

### Fixed

- Fixed Item Pipes using the wrong side of an inventory.

## [0.1.2] - 2020-03-26

### Changed

- Changed the textures slightly.
- Changed the recipes slightly.
- Inventories connected with a pipe and an attachment are no longer marked as a valid destination.

### Added

- Added inventory connector to make placing attachments easier.
- Added new tiers for the Extractor Attachment: Elite and Ultimate.

### Fixed

- Fixed Extractor Attachments not extracting properly.

### Removed

- Removed simple tier for the Item Pipe.

## [0.1.1] - 2020-03-25

Note: Pipes that are placed in the world from version 0.1 will all be removed, since the IDs have changed.

### Changed

- Renamed "Pipe" to "Item Pipe".

### Added

- Added different tiers for the Extractor Attachment: Basic, Improved and Advanced.

### Fixed

- Fixed missing model variant errors.
- Fixed pipes having no drops.

## [0.1.0] - 2020-03-24

### Added

- Initial release.