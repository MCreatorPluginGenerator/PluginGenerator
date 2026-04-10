Procedural blocks will need two fields, "previousStatement" and "nextStatement". 

The first one will indicate if the block can have other procedural blocks before it (which is always the case, so you need to set its value to null). 

The second field will indicate if new procedural blocks can be added after this block. This parameter is set to false with the "Cancel action" block.