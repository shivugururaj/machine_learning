# run the following line once to install the package random
install.packages("random", dependencies = TRUE)
require(random)
craps <- function() {
  field <- c(2,3,12)
  wins <- c(7,11)
  initialRoll <- as.integer(colSums(randomNumbers(2, 1, 6, 1)))
  if (initialRoll %in% field)
    out <- 0
  else if (initialRoll %in% wins)
    out <- 1
  else {
    point <- initialRoll
    # now run the game until you get 7 or point again
    roll <- 0
    while(roll!= point && roll!=7) {
      roll <- as.integer(colSums(randomNumbers(2, 1, 6, 1)))
    }
    if (roll == point)
      out <- 1
    else if (roll == 7)
      out <- 0
    out
  }
  
}

martingale <- function() {
  betAmount <- 100
  gameCount <-  0
  balance  <- 1000
  while ( balance - betAmount > 0 && gameCount < 10){
    result <-craps()
    if(result == 0 ){
      balance <- balance - betAmount
      betAmount <- 2 * betAmount
    }else {
      balance <- balance + betAmount
      betAmount <- 100
    }
    gameCount <- gameCount + 1
  }
  
  print(gameCount)
  print(balance)
}