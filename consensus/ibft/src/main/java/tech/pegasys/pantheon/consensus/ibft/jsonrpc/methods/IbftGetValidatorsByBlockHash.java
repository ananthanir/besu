/*
 * Copyright 2019 ConsenSys AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package tech.pegasys.pantheon.consensus.ibft.jsonrpc.methods;

import tech.pegasys.pantheon.consensus.ibft.IbftBlockInterface;
import tech.pegasys.pantheon.ethereum.chain.Blockchain;
import tech.pegasys.pantheon.ethereum.core.BlockHeader;
import tech.pegasys.pantheon.ethereum.core.Hash;
import tech.pegasys.pantheon.ethereum.jsonrpc.internal.JsonRpcRequest;
import tech.pegasys.pantheon.ethereum.jsonrpc.internal.methods.JsonRpcMethod;
import tech.pegasys.pantheon.ethereum.jsonrpc.internal.parameters.JsonRpcParameter;
import tech.pegasys.pantheon.ethereum.jsonrpc.internal.response.JsonRpcResponse;
import tech.pegasys.pantheon.ethereum.jsonrpc.internal.response.JsonRpcSuccessResponse;

import java.util.Optional;
import java.util.stream.Collectors;

public class IbftGetValidatorsByBlockHash implements JsonRpcMethod {

  private final Blockchain blockchain;
  private final IbftBlockInterface ibftBlockInterface;
  private final JsonRpcParameter parameters;

  public IbftGetValidatorsByBlockHash(
      final Blockchain blockchain,
      final IbftBlockInterface ibftBlockInterface,
      final JsonRpcParameter parameters) {
    this.blockchain = blockchain;
    this.ibftBlockInterface = ibftBlockInterface;
    this.parameters = parameters;
  }

  @Override
  public String getName() {
    return "ibft_getValidatorsByBlockHash";
  }

  @Override
  public JsonRpcResponse response(final JsonRpcRequest request) {
    return new JsonRpcSuccessResponse(request.getId(), blockResult(request));
  }

  private Object blockResult(final JsonRpcRequest request) {
    final Hash hash = parameters.required(request.getParams(), 0, Hash.class);
    final Optional<BlockHeader> blockHeader = blockchain.getBlockHeader(hash);
    return blockHeader
        .map(
            header ->
                ibftBlockInterface
                    .validatorsInBlock(header)
                    .stream()
                    .map(validator -> validator.toString())
                    .collect(Collectors.toList()))
        .orElse(null);
  }
}
