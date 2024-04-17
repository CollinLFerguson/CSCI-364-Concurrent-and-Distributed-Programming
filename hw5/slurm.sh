#!/usr/bin/env bash

#SBATCH --job-name=csci364-hw5
#SBATCH --partition=talon-short
#SBATCH --time=00:10:00
#SBATCH --nodes=1
#SBATCH --ntasks=1
#SBATCH --cpus-per-task=1
#SBATCH --output=%x.%j.txt

module load gcc
module list

echo "Job started at $(date)"
hostname
echo "--"
./hello "<your name>"
echo "Job ended at $(date)"
