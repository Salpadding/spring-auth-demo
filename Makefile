SHELL := $(shell which bash)

init/%:
	mkdir -p $*/src/{main,test}/{java,kotlin,resources}
