#!/bin/bash


# VALIDATION
if ! [ -d "$1" ]; then
  echo "Directory '$1' does not exist."
  printf "USAGE: './cherry-pick.sh <repository_path>'\n"
  exit
fi
echo "Changing directory to '$1'"
cd $1


# SETUP LOCAL BRANCHES
branches=(dev medusa-master werewolf-master unicorn-master chimera-master basilisk-master griffon-master cerberus-master)
git checkout dev


# ASK USER CONFIRM
echo "Check the changes, and if everything is ok, type 'YES'"
read yesOrNo

if [ "$yesOrNo" != "YES" ]; then
  echo "You don't ready with the changes, exiting..."
  exit
fi


# NEW BRANCH FOR COMMIT
branch_name="cherry-pick-$RANDOM"
echo "New branch name is $branch_name"
git checkout -b $branch_name


# COMMITTING CHANGES ANG GETTING HASH
printf "\nWrite message to commit:\n"
read commit_message
git commit -m "$branch_name: $commit_message"
commit_hash=`git rev-parse HEAD`


# CHERRY PICK COMMIT TO EACH BRANCH
for branch in ${branches[@]}; do
  # ASK cherry-pick CONFIRM
  printf "\nAre you sure about cherry-pick to '$branch'? Type 'YES' if you agree\n"
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

  # cherry-pick EXECUTION
  printf "Executing cherry-pick to branch '$branch' from commit '$commit_hash'\n"
  git checkout $branch
  git cherry-pick $commit_hash
  printf "cherry-pick to branch '$branch' from commit '$commit_hash' done!\n\n"
done


## TEAR DOWN
git checkout dev
exit