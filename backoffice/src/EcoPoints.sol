pragma solidity >=0.4.22;

contract TestContract {

  struct EcoPoints {
    uint64 ssn;
    uint32 points;
  }

  mapping(uint64 => EcoPoints) public citizenPoints;
  address admin;

  constructor() public {
    admin = msg.sender;
  }

  function testDeployment() public pure returns (string memory) {
    return "Success!";
  }

  function addPoints(uint64 ssn, uint8 points) public {
    require(admin == msg.sender, "You do not have permission to call this method");
    citizenPoints[ssn] += points;
  }

  function getCitizenPoints(uint64 ssn) public view returns (uint32) {
    return citizenPoints[ssn];
  }
}
