import sys
from time import sleep

print("Ask me what text-adventurer is")
raw_input()

print("I didn't even check what you wrote.")
print("text-adventurer pipes command-line applications into a browser")
sleep(1)
print("It was designed to make *sharing* command line applications easier.")
sleep(1)
print("It boots any command-line application as a subprocess and pipes messages to and from that subprocess via websockets")
sleep(1)
print("The main motivation was to enable teachers to share their pupils' basic command-line work with other pupils, parents, and the internet")
sleep(1)
print("text-adventurer is not secure or robust, just convenient")
sleep(1)
print("Here's a pokemon:")
sleep(1)

with open("bulbasaur.txt", "r") as bulbasaur:
    bulbasaur_ascii = bulbasaur.read()
    print(bulbasaur_ascii)

print("Any more questions?")
question = raw_input()

print("I know you wrote '" + question + "' but, again, I'm not even going to try and handle it. I'm a simple application with no branching. Live with that. See you later.")
