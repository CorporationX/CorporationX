branches=(dev medusa-master werewolf-master unicorn-master chimera-master basilisk-master griffon-master cerberus-master)
for branch in ${branches[@]}; do
  git checkout $branch
  git pull origin
done
git checkout dev
