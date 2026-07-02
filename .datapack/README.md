To enable hotswapping datapacks in a world:
Open a command prompt with admin perms, run the following commands

```
cd [Whatever your project folder is]

mklink /D "run/saves/[World Name]/datapacks/lebois_dev" "[Whatever your project folder is]/.datapack"

git config core.symlinks true

git rm --cached .datapack/data

git checkout .datapack/data
```

Then in run create file allowed_symlinks.txt with the following

```
.datapack
..\src\main\resources\data
```