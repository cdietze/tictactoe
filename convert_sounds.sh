#!/bin/bash
SOUND_PATH="./assets/src/main/resources/assets/sounds"
for x in ${SOUND_PATH}/*.wav; do lame -V2 ${x}; done
