In lieu of a state diagram, the following are the states of the game, and the relevant events for each:
  1. waiting for player to obscure camera
    * camera obscured
  1. counting down start
    * camera unobscured
    * countdown complete
  1. testing steady hand
    * unsteadiness detected
    * timeout
    * camera unobscured
  1. reporting unsteady hand
    * report acknowledged
  1. reporting slow hand
    * report acknowledged
  1. reporting success
    * report acknowledged