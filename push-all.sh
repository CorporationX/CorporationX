#!/bin/bash


# VALIDATION
if ! [ -d "$1" ]; then
  echo "Directory '$1' does not exist."
  printf "USAGE: './push-all.sh <repository_path>'\n"
  exit
fi
echo "Changing directory to '$1'"
cd $1

# PUSH EVERY BRANCH
branches=(dev medusa-master werewolf-master unicorn-master chimera-master basilisk-master griffon-master cerberus-master)

for branch in ${branches[@]}; do
  # ASK push CONFIRM
  printf "\nAre you sure about pushing branch '$branch'? Type 'YES' if you agree\n"
  read yesOrNo
  if [ "$yesOrNo" != "YES" ]; then
    printf "Skipping...\n\n"
    continue
  fi

  # CHECK IF BRANCH EXISTING
  if [ ! `git branch --list $branch` ]
  then
     echo "Branch $branch is not exists"
     continue
  fi

  # PUSH
  printf "Pushing branch '$branch'\n"
  git checkout $branch
  git push
  printf "Branch '$branch' pushed to remote repository!\n\n"
done


## TEAR DOWN
git checkout dev
exit
