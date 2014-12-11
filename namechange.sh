#!/usr/bin bash


git filter-branch -f --commit-filter '
        if [ "$GIT_AUTHOR_NAME" = "JCMS" ];
        then
                GIT_AUTHOR_NAME="Jean-Christophe Lavoie";
               
        fi;
        git commit-tree "$@"' develop
'
