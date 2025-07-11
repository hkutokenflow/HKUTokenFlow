package com.example.workshop1.contracts;

import io.reactivex.Flowable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/LFDT-web3j/web3j/tree/main/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 1.7.0.
 */
@SuppressWarnings("rawtypes")
public class Sc_test extends Contract {
    public static final String BINARY = "608060405234801561001057600080fd5b506040518060400160405280600881526020017f484b55546f6b656e0000000000000000000000000000000000000000000000008152506040518060400160405280600481526020017f484b555400000000000000000000000000000000000000000000000000000000815250816003908161008c9190610766565b50806004908161009c9190610766565b5050506100b26000801b336100ef60201b60201c565b506100ea336100c56101ed60201b60201c565b600a6100d191906109a7565b620f42406100df91906109f2565b6101f660201b60201c565b610b25565b6000610101838361027e60201b60201c565b6101e25760016005600085815260200190815260200160002060000160008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548160ff02191690831515021790555061017f6102e960201b60201c565b73ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff16847f2f8788117e7eff1d82e926ec794901d17c78024a50270940304540a733656f0d60405160405180910390a4600190506101e7565b600090505b92915050565b60006012905090565b600073ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff16036102685760006040517fec442f0500000000000000000000000000000000000000000000000000000000815260040161025f9190610a75565b60405180910390fd5b61027a600083836102f160201b60201c565b5050565b60006005600084815260200190815260200160002060000160008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff16905092915050565b600033905090565b600073ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff16036103435780600260008282546103379190610a90565b92505081905550610416565b60008060008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020549050818110156103cf578381836040517fe450d38c0000000000000000000000000000000000000000000000000000000081526004016103c693929190610ad3565b60405180910390fd5b8181036000808673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002081905550505b600073ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff160361045f57806002600082825403925050819055506104ac565b806000808473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020600082825401925050819055505b8173ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef836040516105099190610b0a565b60405180910390a3505050565b600081519050919050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052604160045260246000fd5b7f4e487b7100000000000000000000000000000000000000000000000000000000600052602260045260246000fd5b6000600282049050600182168061059757607f821691505b6020821081036105aa576105a9610550565b5b50919050565b60008190508160005260206000209050919050565b60006020601f8301049050919050565b600082821b905092915050565b6000600883026106127fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff826105d5565b61061c86836105d5565b95508019841693508086168417925050509392505050565b6000819050919050565b6000819050919050565b600061066361065e61065984610634565b61063e565b610634565b9050919050565b6000819050919050565b61067d83610648565b6106916106898261066a565b8484546105e2565b825550505050565b600090565b6106a6610699565b6106b1818484610674565b505050565b5b818110156106d5576106ca60008261069e565b6001810190506106b7565b5050565b601f82111561071a576106eb816105b0565b6106f4846105c5565b81016020851015610703578190505b61071761070f856105c5565b8301826106b6565b50505b505050565b600082821c905092915050565b600061073d6000198460080261071f565b1980831691505092915050565b6000610756838361072c565b9150826002028217905092915050565b61076f82610516565b67ffffffffffffffff81111561078857610787610521565b5b610792825461057f565b61079d8282856106d9565b600060209050601f8311600181146107d057600084156107be578287015190505b6107c8858261074a565b865550610830565b601f1984166107de866105b0565b60005b82811015610806578489015182556001820191506020850194506020810190506107e1565b86831015610823578489015161081f601f89168261072c565b8355505b6001600288020188555050505b505050505050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052601160045260246000fd5b60008160011c9050919050565b6000808291508390505b60018511156108be5780860481111561089a57610899610838565b5b60018516156108a95780820291505b80810290506108b785610867565b945061087e565b94509492505050565b6000826108d75760019050610993565b816108e55760009050610993565b81600181146108fb576002811461090557610934565b6001915050610993565b60ff84111561091757610916610838565b5b8360020a91508482111561092e5761092d610838565b5b50610993565b5060208310610133831016604e8410600b84101617156109695782820a90508381111561096457610963610838565b5b610993565b6109768484846001610874565b9250905081840481111561098d5761098c610838565b5b81810290505b9392505050565b600060ff82169050919050565b60006109b282610634565b91506109bd8361099a565b92506109ea7fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff84846108c7565b905092915050565b60006109fd82610634565b9150610a0883610634565b9250828202610a1681610634565b91508282048414831517610a2d57610a2c610838565b5b5092915050565b600073ffffffffffffffffffffffffffffffffffffffff82169050919050565b6000610a5f82610a34565b9050919050565b610a6f81610a54565b82525050565b6000602082019050610a8a6000830184610a66565b92915050565b6000610a9b82610634565b9150610aa683610634565b9250828201905080821115610abe57610abd610838565b5b92915050565b610acd81610634565b82525050565b6000606082019050610ae86000830186610a66565b610af56020830185610ac4565b610b026040830184610ac4565b949350505050565b6000602082019050610b1f6000830184610ac4565b92915050565b611bb780610b346000396000f3fe608060405234801561001057600080fd5b50600436106101375760003560e01c806370a08231116100b8578063a9059cbb1161007c578063a9059cbb14610364578063b7dfcbee14610394578063d547741f146103b0578063dd62ed3e146103cc578063f0dda65c146103fc578063f7be43fb1461041857610137565b806370a08231146102aa57806391d14854146102da578063926a694d1461030a57806395d89b4114610328578063a217fddf1461034657610137565b8063248a9ca3116100ff578063248a9ca3146102085780632f2ff15d14610238578063313ce5671461025457806331a95c7a1461027257806336568abe1461028e57610137565b806301ffc9a71461013c57806306fdde031461016c578063095ea7b31461018a57806318160ddd146101ba57806323b872dd146101d8575b600080fd5b61015660048036038101906101519190611460565b610436565b60405161016391906114a8565b60405180910390f35b6101746104b0565b6040516101819190611553565b60405180910390f35b6101a4600480360381019061019f9190611609565b610542565b6040516101b191906114a8565b60405180910390f35b6101c2610565565b6040516101cf9190611658565b60405180910390f35b6101f260048036038101906101ed9190611673565b61056f565b6040516101ff91906114a8565b60405180910390f35b610222600480360381019061021d91906116fc565b61059e565b60405161022f9190611738565b60405180910390f35b610252600480360381019061024d9190611753565b6105be565b005b61025c6105e0565b60405161026991906117af565b60405180910390f35b61028c60048036038101906102879190611673565b6105e9565b005b6102a860048036038101906102a39190611753565b61073e565b005b6102c460048036038101906102bf91906117ca565b6107b9565b6040516102d19190611658565b60405180910390f35b6102f460048036038101906102ef9190611753565b610801565b60405161030191906114a8565b60405180910390f35b61031261086c565b60405161031f9190611738565b60405180910390f35b610330610890565b60405161033d9190611553565b60405180910390f35b61034e610922565b60405161035b9190611738565b60405180910390f35b61037e60048036038101906103799190611609565b610929565b60405161038b91906114a8565b60405180910390f35b6103ae60048036038101906103a9919061192c565b61094c565b005b6103ca60048036038101906103c59190611753565b6109e8565b005b6103e660048036038101906103e19190611988565b610a0a565b6040516103f39190611658565b60405180910390f35b61041660048036038101906104119190611609565b610a91565b005b610420610afb565b60405161042d9190611738565b60405180910390f35b60007f7965db0b000000000000000000000000000000000000000000000000000000007bffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916827bffffffffffffffffffffffffffffffffffffffffffffffffffffffff191614806104a957506104a882610b1f565b5b9050919050565b6060600380546104bf906119f7565b80601f01602080910402602001604051908101604052809291908181526020018280546104eb906119f7565b80156105385780601f1061050d57610100808354040283529160200191610538565b820191906000526020600020905b81548152906001019060200180831161051b57829003601f168201915b5050505050905090565b60008061054d610b89565b905061055a818585610b91565b600191505092915050565b6000600254905090565b60008061057a610b89565b9050610587858285610ba3565b610592858585610c38565b60019150509392505050565b600060056000838152602001908152602001600020600101549050919050565b6105c78261059e565b6105d081610d2c565b6105da8383610d40565b50505050565b60006012905090565b6000801b6105f681610d2c565b6106207fc951d7098b66ba0b8b77265b6e9cf0e187d73125a42bcd0061b09a68be42181085610801565b61065f576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161065690611a74565b60405180910390fd5b6106897f0aaf57387812c7f832412470a57567b5648a930069be49aa9c98d04f88520d3484610801565b6106c8576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016106bf90611a74565b60405180910390fd5b6106d3848484610c38565b8273ffffffffffffffffffffffffffffffffffffffff168473ffffffffffffffffffffffffffffffffffffffff167f2e33fa1fb3bd944a8fefd70eebd6988e59d5c1f348a896213c16b5c5caf04992846040516107309190611658565b60405180910390a350505050565b610746610b89565b73ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff16146107aa576040517f6697b23200000000000000000000000000000000000000000000000000000000815260040160405180910390fd5b6107b48282610e32565b505050565b60008060008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020549050919050565b60006005600084815260200190815260200160002060000160008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff16905092915050565b7f0aaf57387812c7f832412470a57567b5648a930069be49aa9c98d04f88520d3481565b60606004805461089f906119f7565b80601f01602080910402602001604051908101604052809291908181526020018280546108cb906119f7565b80156109185780601f106108ed57610100808354040283529160200191610918565b820191906000526020600020905b8154815290600101906020018083116108fb57829003601f168201915b5050505050905090565b6000801b81565b600080610934610b89565b9050610941818585610c38565b600191505092915050565b6000801b61095981610d2c565b7fc951d7098b66ba0b8b77265b6e9cf0e187d73125a42bcd0061b09a68be4218108280519060200120036109b7576109b17fc951d7098b66ba0b8b77265b6e9cf0e187d73125a42bcd0061b09a68be42181084610d40565b506109e3565b6109e17f0aaf57387812c7f832412470a57567b5648a930069be49aa9c98d04f88520d3484610d40565b505b505050565b6109f18261059e565b6109fa81610d2c565b610a048383610e32565b50505050565b6000600160008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054905092915050565b6000801b610a9e81610d2c565b610aa88383610f25565b8273ffffffffffffffffffffffffffffffffffffffff167f3f2c9d57c068687834f0de942a9babb9e5acab57d516d3480a3c16ee165a427383604051610aee9190611658565b60405180910390a2505050565b7fc951d7098b66ba0b8b77265b6e9cf0e187d73125a42bcd0061b09a68be42181081565b60007f01ffc9a7000000000000000000000000000000000000000000000000000000007bffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916827bffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916149050919050565b600033905090565b610b9e8383836001610fa7565b505050565b6000610baf8484610a0a565b90507fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff811015610c325781811015610c22578281836040517ffb8f41b2000000000000000000000000000000000000000000000000000000008152600401610c1993929190611aa3565b60405180910390fd5b610c3184848484036000610fa7565b5b50505050565b600073ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff1603610caa5760006040517f96c6fd1e000000000000000000000000000000000000000000000000000000008152600401610ca19190611ada565b60405180910390fd5b600073ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff1603610d1c5760006040517fec442f05000000000000000000000000000000000000000000000000000000008152600401610d139190611ada565b60405180910390fd5b610d2783838361117e565b505050565b610d3d81610d38610b89565b6113a3565b50565b6000610d4c8383610801565b610e275760016005600085815260200190815260200160002060000160008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548160ff021916908315150217905550610dc4610b89565b73ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff16847f2f8788117e7eff1d82e926ec794901d17c78024a50270940304540a733656f0d60405160405180910390a460019050610e2c565b600090505b92915050565b6000610e3e8383610801565b15610f1a5760006005600085815260200190815260200160002060000160008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548160ff021916908315150217905550610eb7610b89565b73ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff16847ff6391f5c32d9c69d2a47ea670b442974b53935d1edc7fd64eb21e047a839171b60405160405180910390a460019050610f1f565b600090505b92915050565b600073ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff1603610f975760006040517fec442f05000000000000000000000000000000000000000000000000000000008152600401610f8e9190611ada565b60405180910390fd5b610fa36000838361117e565b5050565b600073ffffffffffffffffffffffffffffffffffffffff168473ffffffffffffffffffffffffffffffffffffffff16036110195760006040517fe602df050000000000000000000000000000000000000000000000000000000081526004016110109190611ada565b60405180910390fd5b600073ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff160361108b5760006040517f94280d620000000000000000000000000000000000000000000000000000000081526004016110829190611ada565b60405180910390fd5b81600160008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055508015611178578273ffffffffffffffffffffffffffffffffffffffff168473ffffffffffffffffffffffffffffffffffffffff167f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b9258460405161116f9190611658565b60405180910390a35b50505050565b600073ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff16036111d05780600260008282546111c49190611b24565b925050819055506112a3565b60008060008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000205490508181101561125c578381836040517fe450d38c00000000000000000000000000000000000000000000000000000000815260040161125393929190611aa3565b60405180910390fd5b8181036000808673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002081905550505b600073ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff16036112ec5780600260008282540392505081905550611339565b806000808473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020600082825401925050819055505b8173ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef836040516113969190611658565b60405180910390a3505050565b6113ad8282610801565b6113f05780826040517fe2517d3f0000000000000000000000000000000000000000000000000000000081526004016113e7929190611b58565b60405180910390fd5b5050565b6000604051905090565b600080fd5b600080fd5b60007fffffffff0000000000000000000000000000000000000000000000000000000082169050919050565b61143d81611408565b811461144857600080fd5b50565b60008135905061145a81611434565b92915050565b600060208284031215611476576114756113fe565b5b60006114848482850161144b565b91505092915050565b60008115159050919050565b6114a28161148d565b82525050565b60006020820190506114bd6000830184611499565b92915050565b600081519050919050565b600082825260208201905092915050565b60005b838110156114fd5780820151818401526020810190506114e2565b60008484015250505050565b6000601f19601f8301169050919050565b6000611525826114c3565b61152f81856114ce565b935061153f8185602086016114df565b61154881611509565b840191505092915050565b6000602082019050818103600083015261156d818461151a565b905092915050565b600073ffffffffffffffffffffffffffffffffffffffff82169050919050565b60006115a082611575565b9050919050565b6115b081611595565b81146115bb57600080fd5b50565b6000813590506115cd816115a7565b92915050565b6000819050919050565b6115e6816115d3565b81146115f157600080fd5b50565b600081359050611603816115dd565b92915050565b600080604083850312156116205761161f6113fe565b5b600061162e858286016115be565b925050602061163f858286016115f4565b9150509250929050565b611652816115d3565b82525050565b600060208201905061166d6000830184611649565b92915050565b60008060006060848603121561168c5761168b6113fe565b5b600061169a868287016115be565b93505060206116ab868287016115be565b92505060406116bc868287016115f4565b9150509250925092565b6000819050919050565b6116d9816116c6565b81146116e457600080fd5b50565b6000813590506116f6816116d0565b92915050565b600060208284031215611712576117116113fe565b5b6000611720848285016116e7565b91505092915050565b611732816116c6565b82525050565b600060208201905061174d6000830184611729565b92915050565b6000806040838503121561176a576117696113fe565b5b6000611778858286016116e7565b9250506020611789858286016115be565b9150509250929050565b600060ff82169050919050565b6117a981611793565b82525050565b60006020820190506117c460008301846117a0565b92915050565b6000602082840312156117e0576117df6113fe565b5b60006117ee848285016115be565b91505092915050565b600080fd5b600080fd5b7f4e487b7100000000000000000000000000000000000000000000000000000000600052604160045260246000fd5b61183982611509565b810181811067ffffffffffffffff8211171561185857611857611801565b5b80604052505050565b600061186b6113f4565b90506118778282611830565b919050565b600067ffffffffffffffff82111561189757611896611801565b5b6118a082611509565b9050602081019050919050565b82818337600083830152505050565b60006118cf6118ca8461187c565b611861565b9050828152602081018484840111156118eb576118ea6117fc565b5b6118f68482856118ad565b509392505050565b600082601f830112611913576119126117f7565b5b81356119238482602086016118bc565b91505092915050565b60008060408385031215611943576119426113fe565b5b6000611951858286016115be565b925050602083013567ffffffffffffffff81111561197257611971611403565b5b61197e858286016118fe565b9150509250929050565b6000806040838503121561199f5761199e6113fe565b5b60006119ad858286016115be565b92505060206119be858286016115be565b9150509250929050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052602260045260246000fd5b60006002820490506001821680611a0f57607f821691505b602082108103611a2257611a216119c8565b5b50919050565b7f496e76616c69642076656e646f72000000000000000000000000000000000000600082015250565b6000611a5e600e836114ce565b9150611a6982611a28565b602082019050919050565b60006020820190508181036000830152611a8d81611a51565b9050919050565b611a9d81611595565b82525050565b6000606082019050611ab86000830186611a94565b611ac56020830185611649565b611ad26040830184611649565b949350505050565b6000602082019050611aef6000830184611a94565b92915050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052601160045260246000fd5b6000611b2f826115d3565b9150611b3a836115d3565b9250828201905080821115611b5257611b51611af5565b5b92915050565b6000604082019050611b6d6000830185611a94565b611b7a6020830184611729565b939250505056fea2646970667358221220acf9a05fb84e2b4687f52f224413461f9fdd2a64ddaffe7f60b9d6639f274c2964736f6c634300081e0033";

    public static final String FUNC_APPROVE = "approve";

    public static final String FUNC_ASSIGNROLE = "assignRole";

    public static final String FUNC_GRANTROLE = "grantRole";

    public static final String FUNC_MINTTOKENS = "mintTokens";

    public static final String FUNC_REDEEMTOKENS = "redeemTokens";

    public static final String FUNC_RENOUNCEROLE = "renounceRole";

    public static final String FUNC_REVOKEROLE = "revokeRole";

    public static final String FUNC_TRANSFER = "transfer";

    public static final String FUNC_TRANSFERFROM = "transferFrom";

    public static final String FUNC_ALLOWANCE = "allowance";

    public static final String FUNC_BALANCEOF = "balanceOf";

    public static final String FUNC_DECIMALS = "decimals";

    public static final String FUNC_DEFAULT_ADMIN_ROLE = "DEFAULT_ADMIN_ROLE";

    public static final String FUNC_GETROLEADMIN = "getRoleAdmin";

    public static final String FUNC_HASROLE = "hasRole";

    public static final String FUNC_NAME = "name";

    public static final String FUNC_STUDENT_ROLE = "STUDENT_ROLE";

    public static final String FUNC_SUPPORTSINTERFACE = "supportsInterface";

    public static final String FUNC_SYMBOL = "symbol";

    public static final String FUNC_TOTALSUPPLY = "totalSupply";

    public static final String FUNC_VENDOR_ROLE = "VENDOR_ROLE";
    ;

    public static final Event APPROVAL_EVENT = new Event("Approval", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event ROLEADMINCHANGED_EVENT = new Event("RoleAdminChanged", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>(true) {}, new TypeReference<Bytes32>(true) {}, new TypeReference<Bytes32>(true) {}));
    ;

    public static final Event ROLEGRANTED_EVENT = new Event("RoleGranted", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}));
    ;

    public static final Event ROLEREVOKED_EVENT = new Event("RoleRevoked", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}));
    ;

    public static final Event TOKENSMINTED_EVENT = new Event("TokensMinted", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event TOKENSREDEEMED_EVENT = new Event("TokensRedeemed", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event TRANSFER_EVENT = new Event("Transfer", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    @Deprecated
    protected Sc_test(String contractAddress, Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected Sc_test(String contractAddress, Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected Sc_test(String contractAddress, Web3j web3j, TransactionManager transactionManager,
            BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected Sc_test(String contractAddress, Web3j web3j, TransactionManager transactionManager,
            ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteFunctionCall<TransactionReceipt> approve(String spender, BigInteger value) {
        final Function function = new Function(
                FUNC_APPROVE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, spender), 
                new org.web3j.abi.datatypes.generated.Uint256(value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public static List<ApprovalEventResponse> getApprovalEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(APPROVAL_EVENT, transactionReceipt);
        ArrayList<ApprovalEventResponse> responses = new ArrayList<ApprovalEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ApprovalEventResponse typedResponse = new ApprovalEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.owner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.spender = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static ApprovalEventResponse getApprovalEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(APPROVAL_EVENT, log);
        ApprovalEventResponse typedResponse = new ApprovalEventResponse();
        typedResponse.log = log;
        typedResponse.owner = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.spender = (String) eventValues.getIndexedValues().get(1).getValue();
        typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<ApprovalEventResponse> approvalEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getApprovalEventFromLog(log));
    }

    public Flowable<ApprovalEventResponse> approvalEventFlowable(DefaultBlockParameter startBlock,
            DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(APPROVAL_EVENT));
        return approvalEventFlowable(filter);
    }

    public RemoteFunctionCall<TransactionReceipt> assignRole(String user, String role) {
        final Function function = new Function(
                FUNC_ASSIGNROLE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, user), 
                new org.web3j.abi.datatypes.Utf8String(role)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> grantRole(byte[] role, String account) {
        final Function function = new Function(
                FUNC_GRANTROLE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(role), 
                new org.web3j.abi.datatypes.Address(160, account)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> mintTokens(String to, BigInteger amount) {
        final Function function = new Function(
                FUNC_MINTTOKENS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, to), 
                new org.web3j.abi.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> redeemTokens(String from, String to,
            BigInteger amount) {
        final Function function = new Function(
                FUNC_REDEEMTOKENS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, from), 
                new org.web3j.abi.datatypes.Address(160, to), 
                new org.web3j.abi.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> renounceRole(byte[] role,
            String callerConfirmation) {
        final Function function = new Function(
                FUNC_RENOUNCEROLE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(role), 
                new org.web3j.abi.datatypes.Address(160, callerConfirmation)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> revokeRole(byte[] role, String account) {
        final Function function = new Function(
                FUNC_REVOKEROLE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(role), 
                new org.web3j.abi.datatypes.Address(160, account)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public static List<RoleAdminChangedEventResponse> getRoleAdminChangedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(ROLEADMINCHANGED_EVENT, transactionReceipt);
        ArrayList<RoleAdminChangedEventResponse> responses = new ArrayList<RoleAdminChangedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            RoleAdminChangedEventResponse typedResponse = new RoleAdminChangedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.role = (byte[]) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.previousAdminRole = (byte[]) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.newAdminRole = (byte[]) eventValues.getIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static RoleAdminChangedEventResponse getRoleAdminChangedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(ROLEADMINCHANGED_EVENT, log);
        RoleAdminChangedEventResponse typedResponse = new RoleAdminChangedEventResponse();
        typedResponse.log = log;
        typedResponse.role = (byte[]) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.previousAdminRole = (byte[]) eventValues.getIndexedValues().get(1).getValue();
        typedResponse.newAdminRole = (byte[]) eventValues.getIndexedValues().get(2).getValue();
        return typedResponse;
    }

    public Flowable<RoleAdminChangedEventResponse> roleAdminChangedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getRoleAdminChangedEventFromLog(log));
    }

    public Flowable<RoleAdminChangedEventResponse> roleAdminChangedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(ROLEADMINCHANGED_EVENT));
        return roleAdminChangedEventFlowable(filter);
    }

    public static List<RoleGrantedEventResponse> getRoleGrantedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(ROLEGRANTED_EVENT, transactionReceipt);
        ArrayList<RoleGrantedEventResponse> responses = new ArrayList<RoleGrantedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            RoleGrantedEventResponse typedResponse = new RoleGrantedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.role = (byte[]) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.account = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.sender = (String) eventValues.getIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static RoleGrantedEventResponse getRoleGrantedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(ROLEGRANTED_EVENT, log);
        RoleGrantedEventResponse typedResponse = new RoleGrantedEventResponse();
        typedResponse.log = log;
        typedResponse.role = (byte[]) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.account = (String) eventValues.getIndexedValues().get(1).getValue();
        typedResponse.sender = (String) eventValues.getIndexedValues().get(2).getValue();
        return typedResponse;
    }

    public Flowable<RoleGrantedEventResponse> roleGrantedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getRoleGrantedEventFromLog(log));
    }

    public Flowable<RoleGrantedEventResponse> roleGrantedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(ROLEGRANTED_EVENT));
        return roleGrantedEventFlowable(filter);
    }

    public static List<RoleRevokedEventResponse> getRoleRevokedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(ROLEREVOKED_EVENT, transactionReceipt);
        ArrayList<RoleRevokedEventResponse> responses = new ArrayList<RoleRevokedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            RoleRevokedEventResponse typedResponse = new RoleRevokedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.role = (byte[]) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.account = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.sender = (String) eventValues.getIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static RoleRevokedEventResponse getRoleRevokedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(ROLEREVOKED_EVENT, log);
        RoleRevokedEventResponse typedResponse = new RoleRevokedEventResponse();
        typedResponse.log = log;
        typedResponse.role = (byte[]) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.account = (String) eventValues.getIndexedValues().get(1).getValue();
        typedResponse.sender = (String) eventValues.getIndexedValues().get(2).getValue();
        return typedResponse;
    }

    public Flowable<RoleRevokedEventResponse> roleRevokedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getRoleRevokedEventFromLog(log));
    }

    public Flowable<RoleRevokedEventResponse> roleRevokedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(ROLEREVOKED_EVENT));
        return roleRevokedEventFlowable(filter);
    }

    public static List<TokensMintedEventResponse> getTokensMintedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(TOKENSMINTED_EVENT, transactionReceipt);
        ArrayList<TokensMintedEventResponse> responses = new ArrayList<TokensMintedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            TokensMintedEventResponse typedResponse = new TokensMintedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.to = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static TokensMintedEventResponse getTokensMintedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(TOKENSMINTED_EVENT, log);
        TokensMintedEventResponse typedResponse = new TokensMintedEventResponse();
        typedResponse.log = log;
        typedResponse.to = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<TokensMintedEventResponse> tokensMintedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getTokensMintedEventFromLog(log));
    }

    public Flowable<TokensMintedEventResponse> tokensMintedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(TOKENSMINTED_EVENT));
        return tokensMintedEventFlowable(filter);
    }

    public static List<TokensRedeemedEventResponse> getTokensRedeemedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(TOKENSREDEEMED_EVENT, transactionReceipt);
        ArrayList<TokensRedeemedEventResponse> responses = new ArrayList<TokensRedeemedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            TokensRedeemedEventResponse typedResponse = new TokensRedeemedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static TokensRedeemedEventResponse getTokensRedeemedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(TOKENSREDEEMED_EVENT, log);
        TokensRedeemedEventResponse typedResponse = new TokensRedeemedEventResponse();
        typedResponse.log = log;
        typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
        typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<TokensRedeemedEventResponse> tokensRedeemedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getTokensRedeemedEventFromLog(log));
    }

    public Flowable<TokensRedeemedEventResponse> tokensRedeemedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(TOKENSREDEEMED_EVENT));
        return tokensRedeemedEventFlowable(filter);
    }

    public RemoteFunctionCall<TransactionReceipt> transfer(String to, BigInteger value) {
        final Function function = new Function(
                FUNC_TRANSFER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, to), 
                new org.web3j.abi.datatypes.generated.Uint256(value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public static List<TransferEventResponse> getTransferEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(TRANSFER_EVENT, transactionReceipt);
        ArrayList<TransferEventResponse> responses = new ArrayList<TransferEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            TransferEventResponse typedResponse = new TransferEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static TransferEventResponse getTransferEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(TRANSFER_EVENT, log);
        TransferEventResponse typedResponse = new TransferEventResponse();
        typedResponse.log = log;
        typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
        typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<TransferEventResponse> transferEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getTransferEventFromLog(log));
    }

    public Flowable<TransferEventResponse> transferEventFlowable(DefaultBlockParameter startBlock,
            DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(TRANSFER_EVENT));
        return transferEventFlowable(filter);
    }

    public RemoteFunctionCall<TransactionReceipt> transferFrom(String from, String to,
            BigInteger value) {
        final Function function = new Function(
                FUNC_TRANSFERFROM, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, from), 
                new org.web3j.abi.datatypes.Address(160, to), 
                new org.web3j.abi.datatypes.generated.Uint256(value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> allowance(String owner, String spender) {
        final Function function = new Function(FUNC_ALLOWANCE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, owner), 
                new org.web3j.abi.datatypes.Address(160, spender)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> balanceOf(String account) {
        final Function function = new Function(FUNC_BALANCEOF, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, account)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> decimals() {
        final Function function = new Function(FUNC_DECIMALS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<byte[]> DEFAULT_ADMIN_ROLE() {
        final Function function = new Function(FUNC_DEFAULT_ADMIN_ROLE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteFunctionCall<byte[]> getRoleAdmin(byte[] role) {
        final Function function = new Function(FUNC_GETROLEADMIN, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(role)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteFunctionCall<Boolean> hasRole(byte[] role, String account) {
        final Function function = new Function(FUNC_HASROLE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(role), 
                new org.web3j.abi.datatypes.Address(160, account)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<String> name() {
        final Function function = new Function(FUNC_NAME, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<byte[]> STUDENT_ROLE() {
        final Function function = new Function(FUNC_STUDENT_ROLE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteFunctionCall<Boolean> supportsInterface(byte[] interfaceId) {
        final Function function = new Function(FUNC_SUPPORTSINTERFACE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes4(interfaceId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<String> symbol() {
        final Function function = new Function(FUNC_SYMBOL, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<BigInteger> totalSupply() {
        final Function function = new Function(FUNC_TOTALSUPPLY, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<byte[]> VENDOR_ROLE() {
        final Function function = new Function(FUNC_VENDOR_ROLE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    @Deprecated
    public static Sc_test load(String contractAddress, Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit) {
        return new Sc_test(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static Sc_test load(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new Sc_test(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static Sc_test load(String contractAddress, Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider) {
        return new Sc_test(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static Sc_test load(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new Sc_test(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<Sc_test> deploy(Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider) {
        return deployRemoteCall(Sc_test.class, web3j, credentials, contractGasProvider, getDeploymentBinary(), "");
    }

    public static RemoteCall<Sc_test> deploy(Web3j web3j, TransactionManager transactionManager,
            ContractGasProvider contractGasProvider) {
        return deployRemoteCall(Sc_test.class, web3j, transactionManager, contractGasProvider, getDeploymentBinary(), "");
    }

    @Deprecated
    public static RemoteCall<Sc_test> deploy(Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Sc_test.class, web3j, credentials, gasPrice, gasLimit, getDeploymentBinary(), "");
    }

    @Deprecated
    public static RemoteCall<Sc_test> deploy(Web3j web3j, TransactionManager transactionManager,
            BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Sc_test.class, web3j, transactionManager, gasPrice, gasLimit, getDeploymentBinary(), "");
    }

    private static String getDeploymentBinary() {
        return BINARY;
    }

    public static class ApprovalEventResponse extends BaseEventResponse {
        public String owner;

        public String spender;

        public BigInteger value;
    }

    public static class RoleAdminChangedEventResponse extends BaseEventResponse {
        public byte[] role;

        public byte[] previousAdminRole;

        public byte[] newAdminRole;
    }

    public static class RoleGrantedEventResponse extends BaseEventResponse {
        public byte[] role;

        public String account;

        public String sender;
    }

    public static class RoleRevokedEventResponse extends BaseEventResponse {
        public byte[] role;

        public String account;

        public String sender;
    }

    public static class TokensMintedEventResponse extends BaseEventResponse {
        public String to;

        public BigInteger amount;
    }

    public static class TokensRedeemedEventResponse extends BaseEventResponse {
        public String from;

        public String to;

        public BigInteger amount;
    }

    public static class TransferEventResponse extends BaseEventResponse {
        public String from;

        public String to;

        public BigInteger value;
    }
}
