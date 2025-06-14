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
    public static final String BINARY = "608060405234801561001057600080fd5b506040518060400160405280600881526020017f484b55546f6b656e0000000000000000000000000000000000000000000000008152506040518060400160405280600481526020017f484b555400000000000000000000000000000000000000000000000000000000815250816003908161008c9190610766565b50806004908161009c9190610766565b5050506100b26000801b336100ef60201b60201c565b506100ea336100c56101ed60201b60201c565b600a6100d191906109a7565b620f42406100df91906109f2565b6101f660201b60201c565b610b25565b6000610101838361027e60201b60201c565b6101e25760016005600085815260200190815260200160002060000160008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548160ff02191690831515021790555061017f6102e960201b60201c565b73ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff16847f2f8788117e7eff1d82e926ec794901d17c78024a50270940304540a733656f0d60405160405180910390a4600190506101e7565b600090505b92915050565b60006012905090565b600073ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff16036102685760006040517fec442f0500000000000000000000000000000000000000000000000000000000815260040161025f9190610a75565b60405180910390fd5b61027a600083836102f160201b60201c565b5050565b60006005600084815260200190815260200160002060000160008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff16905092915050565b600033905090565b600073ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff16036103435780600260008282546103379190610a90565b92505081905550610416565b60008060008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020549050818110156103cf578381836040517fe450d38c0000000000000000000000000000000000000000000000000000000081526004016103c693929190610ad3565b60405180910390fd5b8181036000808673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002081905550505b600073ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff160361045f57806002600082825403925050819055506104ac565b806000808473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020600082825401925050819055505b8173ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef836040516105099190610b0a565b60405180910390a3505050565b600081519050919050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052604160045260246000fd5b7f4e487b7100000000000000000000000000000000000000000000000000000000600052602260045260246000fd5b6000600282049050600182168061059757607f821691505b6020821081036105aa576105a9610550565b5b50919050565b60008190508160005260206000209050919050565b60006020601f8301049050919050565b600082821b905092915050565b6000600883026106127fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff826105d5565b61061c86836105d5565b95508019841693508086168417925050509392505050565b6000819050919050565b6000819050919050565b600061066361065e61065984610634565b61063e565b610634565b9050919050565b6000819050919050565b61067d83610648565b6106916106898261066a565b8484546105e2565b825550505050565b600090565b6106a6610699565b6106b1818484610674565b505050565b5b818110156106d5576106ca60008261069e565b6001810190506106b7565b5050565b601f82111561071a576106eb816105b0565b6106f4846105c5565b81016020851015610703578190505b61071761070f856105c5565b8301826106b6565b50505b505050565b600082821c905092915050565b600061073d6000198460080261071f565b1980831691505092915050565b6000610756838361072c565b9150826002028217905092915050565b61076f82610516565b67ffffffffffffffff81111561078857610787610521565b5b610792825461057f565b61079d8282856106d9565b600060209050601f8311600181146107d057600084156107be578287015190505b6107c8858261074a565b865550610830565b601f1984166107de866105b0565b60005b82811015610806578489015182556001820191506020850194506020810190506107e1565b86831015610823578489015161081f601f89168261072c565b8355505b6001600288020188555050505b505050505050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052601160045260246000fd5b60008160011c9050919050565b6000808291508390505b60018511156108be5780860481111561089a57610899610838565b5b60018516156108a95780820291505b80810290506108b785610867565b945061087e565b94509492505050565b6000826108d75760019050610993565b816108e55760009050610993565b81600181146108fb576002811461090557610934565b6001915050610993565b60ff84111561091757610916610838565b5b8360020a91508482111561092e5761092d610838565b5b50610993565b5060208310610133831016604e8410600b84101617156109695782820a90508381111561096457610963610838565b5b610993565b6109768484846001610874565b9250905081840481111561098d5761098c610838565b5b81810290505b9392505050565b600060ff82169050919050565b60006109b282610634565b91506109bd8361099a565b92506109ea7fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff84846108c7565b905092915050565b60006109fd82610634565b9150610a0883610634565b9250828202610a1681610634565b91508282048414831517610a2d57610a2c610838565b5b5092915050565b600073ffffffffffffffffffffffffffffffffffffffff82169050919050565b6000610a5f82610a34565b9050919050565b610a6f81610a54565b82525050565b6000602082019050610a8a6000830184610a66565b92915050565b6000610a9b82610634565b9150610aa683610634565b9250828201905080821115610abe57610abd610838565b5b92915050565b610acd81610634565b82525050565b6000606082019050610ae86000830186610a66565b610af56020830185610ac4565b610b026040830184610ac4565b949350505050565b6000602082019050610b1f6000830184610ac4565b92915050565b611b6b80610b346000396000f3fe608060405234801561001057600080fd5b50600436106101375760003560e01c806370a08231116100b8578063a9059cbb1161007c578063a9059cbb14610364578063b7dfcbee14610394578063d547741f146103b0578063dd62ed3e146103cc578063f0dda65c146103fc578063f7be43fb1461041857610137565b806370a08231146102aa57806391d14854146102da578063926a694d1461030a57806395d89b4114610328578063a217fddf1461034657610137565b8063248a9ca3116100ff578063248a9ca3146102085780632f2ff15d14610238578063313ce5671461025457806331a95c7a1461027257806336568abe1461028e57610137565b806301ffc9a71461013c57806306fdde031461016c578063095ea7b31461018a57806318160ddd146101ba57806323b872dd146101d8575b600080fd5b61015660048036038101906101519190611414565b610436565b604051610163919061145c565b60405180910390f35b6101746104b0565b6040516101819190611507565b60405180910390f35b6101a4600480360381019061019f91906115bd565b610542565b6040516101b1919061145c565b60405180910390f35b6101c2610565565b6040516101cf919061160c565b60405180910390f35b6101f260048036038101906101ed9190611627565b61056f565b6040516101ff919061145c565b60405180910390f35b610222600480360381019061021d91906116b0565b61059e565b60405161022f91906116ec565b60405180910390f35b610252600480360381019061024d9190611707565b6105be565b005b61025c6105e0565b6040516102699190611763565b60405180910390f35b61028c60048036038101906102879190611627565b6105e9565b005b6102a860048036038101906102a39190611707565b6106f2565b005b6102c460048036038101906102bf919061177e565b61076d565b6040516102d1919061160c565b60405180910390f35b6102f460048036038101906102ef9190611707565b6107b5565b604051610301919061145c565b60405180910390f35b610312610820565b60405161031f91906116ec565b60405180910390f35b610330610844565b60405161033d9190611507565b60405180910390f35b61034e6108d6565b60405161035b91906116ec565b60405180910390f35b61037e600480360381019061037991906115bd565b6108dd565b60405161038b919061145c565b60405180910390f35b6103ae60048036038101906103a991906118e0565b610900565b005b6103ca60048036038101906103c59190611707565b61099c565b005b6103e660048036038101906103e1919061193c565b6109be565b6040516103f3919061160c565b60405180910390f35b610416600480360381019061041191906115bd565b610a45565b005b610420610aaf565b60405161042d91906116ec565b60405180910390f35b60007f7965db0b000000000000000000000000000000000000000000000000000000007bffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916827bffffffffffffffffffffffffffffffffffffffffffffffffffffffff191614806104a957506104a882610ad3565b5b9050919050565b6060600380546104bf906119ab565b80601f01602080910402602001604051908101604052809291908181526020018280546104eb906119ab565b80156105385780601f1061050d57610100808354040283529160200191610538565b820191906000526020600020905b81548152906001019060200180831161051b57829003601f168201915b5050505050905090565b60008061054d610b3d565b905061055a818585610b45565b600191505092915050565b6000600254905090565b60008061057a610b3d565b9050610587858285610b57565b610592858585610bec565b60019150509392505050565b600060056000838152602001908152602001600020600101549050919050565b6105c78261059e565b6105d081610ce0565b6105da8383610cf4565b50505050565b60006012905090565b7fc951d7098b66ba0b8b77265b6e9cf0e187d73125a42bcd0061b09a68be42181061061381610ce0565b61063d7f0aaf57387812c7f832412470a57567b5648a930069be49aa9c98d04f88520d34846107b5565b61067c576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161067390611a28565b60405180910390fd5b610687848484610bec565b8273ffffffffffffffffffffffffffffffffffffffff168473ffffffffffffffffffffffffffffffffffffffff167f2e33fa1fb3bd944a8fefd70eebd6988e59d5c1f348a896213c16b5c5caf04992846040516106e4919061160c565b60405180910390a350505050565b6106fa610b3d565b73ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff161461075e576040517f6697b23200000000000000000000000000000000000000000000000000000000815260040160405180910390fd5b6107688282610de6565b505050565b60008060008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020549050919050565b60006005600084815260200190815260200160002060000160008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff16905092915050565b7f0aaf57387812c7f832412470a57567b5648a930069be49aa9c98d04f88520d3481565b606060048054610853906119ab565b80601f016020809104026020016040519081016040528092919081815260200182805461087f906119ab565b80156108cc5780601f106108a1576101008083540402835291602001916108cc565b820191906000526020600020905b8154815290600101906020018083116108af57829003601f168201915b5050505050905090565b6000801b81565b6000806108e8610b3d565b90506108f5818585610bec565b600191505092915050565b6000801b61090d81610ce0565b7fc951d7098b66ba0b8b77265b6e9cf0e187d73125a42bcd0061b09a68be42181082805190602001200361096b576109657fc951d7098b66ba0b8b77265b6e9cf0e187d73125a42bcd0061b09a68be42181084610cf4565b50610997565b6109957f0aaf57387812c7f832412470a57567b5648a930069be49aa9c98d04f88520d3484610cf4565b505b505050565b6109a58261059e565b6109ae81610ce0565b6109b88383610de6565b50505050565b6000600160008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054905092915050565b6000801b610a5281610ce0565b610a5c8383610ed9565b8273ffffffffffffffffffffffffffffffffffffffff167f3f2c9d57c068687834f0de942a9babb9e5acab57d516d3480a3c16ee165a427383604051610aa2919061160c565b60405180910390a2505050565b7fc951d7098b66ba0b8b77265b6e9cf0e187d73125a42bcd0061b09a68be42181081565b60007f01ffc9a7000000000000000000000000000000000000000000000000000000007bffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916827bffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916149050919050565b600033905090565b610b528383836001610f5b565b505050565b6000610b6384846109be565b90507fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff811015610be65781811015610bd6578281836040517ffb8f41b2000000000000000000000000000000000000000000000000000000008152600401610bcd93929190611a57565b60405180910390fd5b610be584848484036000610f5b565b5b50505050565b600073ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff1603610c5e5760006040517f96c6fd1e000000000000000000000000000000000000000000000000000000008152600401610c559190611a8e565b60405180910390fd5b600073ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff1603610cd05760006040517fec442f05000000000000000000000000000000000000000000000000000000008152600401610cc79190611a8e565b60405180910390fd5b610cdb838383611132565b505050565b610cf181610cec610b3d565b611357565b50565b6000610d0083836107b5565b610ddb5760016005600085815260200190815260200160002060000160008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548160ff021916908315150217905550610d78610b3d565b73ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff16847f2f8788117e7eff1d82e926ec794901d17c78024a50270940304540a733656f0d60405160405180910390a460019050610de0565b600090505b92915050565b6000610df283836107b5565b15610ece5760006005600085815260200190815260200160002060000160008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548160ff021916908315150217905550610e6b610b3d565b73ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff16847ff6391f5c32d9c69d2a47ea670b442974b53935d1edc7fd64eb21e047a839171b60405160405180910390a460019050610ed3565b600090505b92915050565b600073ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff1603610f4b5760006040517fec442f05000000000000000000000000000000000000000000000000000000008152600401610f429190611a8e565b60405180910390fd5b610f5760008383611132565b5050565b600073ffffffffffffffffffffffffffffffffffffffff168473ffffffffffffffffffffffffffffffffffffffff1603610fcd5760006040517fe602df05000000000000000000000000000000000000000000000000000000008152600401610fc49190611a8e565b60405180910390fd5b600073ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff160361103f5760006040517f94280d620000000000000000000000000000000000000000000000000000000081526004016110369190611a8e565b60405180910390fd5b81600160008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002081905550801561112c578273ffffffffffffffffffffffffffffffffffffffff168473ffffffffffffffffffffffffffffffffffffffff167f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b92584604051611123919061160c565b60405180910390a35b50505050565b600073ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff16036111845780600260008282546111789190611ad8565b92505081905550611257565b60008060008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054905081811015611210578381836040517fe450d38c00000000000000000000000000000000000000000000000000000000815260040161120793929190611a57565b60405180910390fd5b8181036000808673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002081905550505b600073ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff16036112a057806002600082825403925050819055506112ed565b806000808473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020600082825401925050819055505b8173ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef8360405161134a919061160c565b60405180910390a3505050565b61136182826107b5565b6113a45780826040517fe2517d3f00000000000000000000000000000000000000000000000000000000815260040161139b929190611b0c565b60405180910390fd5b5050565b6000604051905090565b600080fd5b600080fd5b60007fffffffff0000000000000000000000000000000000000000000000000000000082169050919050565b6113f1816113bc565b81146113fc57600080fd5b50565b60008135905061140e816113e8565b92915050565b60006020828403121561142a576114296113b2565b5b6000611438848285016113ff565b91505092915050565b60008115159050919050565b61145681611441565b82525050565b6000602082019050611471600083018461144d565b92915050565b600081519050919050565b600082825260208201905092915050565b60005b838110156114b1578082015181840152602081019050611496565b60008484015250505050565b6000601f19601f8301169050919050565b60006114d982611477565b6114e38185611482565b93506114f3818560208601611493565b6114fc816114bd565b840191505092915050565b6000602082019050818103600083015261152181846114ce565b905092915050565b600073ffffffffffffffffffffffffffffffffffffffff82169050919050565b600061155482611529565b9050919050565b61156481611549565b811461156f57600080fd5b50565b6000813590506115818161155b565b92915050565b6000819050919050565b61159a81611587565b81146115a557600080fd5b50565b6000813590506115b781611591565b92915050565b600080604083850312156115d4576115d36113b2565b5b60006115e285828601611572565b92505060206115f3858286016115a8565b9150509250929050565b61160681611587565b82525050565b600060208201905061162160008301846115fd565b92915050565b6000806000606084860312156116405761163f6113b2565b5b600061164e86828701611572565b935050602061165f86828701611572565b9250506040611670868287016115a8565b9150509250925092565b6000819050919050565b61168d8161167a565b811461169857600080fd5b50565b6000813590506116aa81611684565b92915050565b6000602082840312156116c6576116c56113b2565b5b60006116d48482850161169b565b91505092915050565b6116e68161167a565b82525050565b600060208201905061170160008301846116dd565b92915050565b6000806040838503121561171e5761171d6113b2565b5b600061172c8582860161169b565b925050602061173d85828601611572565b9150509250929050565b600060ff82169050919050565b61175d81611747565b82525050565b60006020820190506117786000830184611754565b92915050565b600060208284031215611794576117936113b2565b5b60006117a284828501611572565b91505092915050565b600080fd5b600080fd5b7f4e487b7100000000000000000000000000000000000000000000000000000000600052604160045260246000fd5b6117ed826114bd565b810181811067ffffffffffffffff8211171561180c5761180b6117b5565b5b80604052505050565b600061181f6113a8565b905061182b82826117e4565b919050565b600067ffffffffffffffff82111561184b5761184a6117b5565b5b611854826114bd565b9050602081019050919050565b82818337600083830152505050565b600061188361187e84611830565b611815565b90508281526020810184848401111561189f5761189e6117b0565b5b6118aa848285611861565b509392505050565b600082601f8301126118c7576118c66117ab565b5b81356118d7848260208601611870565b91505092915050565b600080604083850312156118f7576118f66113b2565b5b600061190585828601611572565b925050602083013567ffffffffffffffff811115611926576119256113b7565b5b611932858286016118b2565b9150509250929050565b60008060408385031215611953576119526113b2565b5b600061196185828601611572565b925050602061197285828601611572565b9150509250929050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052602260045260246000fd5b600060028204905060018216806119c357607f821691505b6020821081036119d6576119d561197c565b5b50919050565b7f496e76616c69642076656e646f72000000000000000000000000000000000000600082015250565b6000611a12600e83611482565b9150611a1d826119dc565b602082019050919050565b60006020820190508181036000830152611a4181611a05565b9050919050565b611a5181611549565b82525050565b6000606082019050611a6c6000830186611a48565b611a7960208301856115fd565b611a8660408301846115fd565b949350505050565b6000602082019050611aa36000830184611a48565b92915050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052601160045260246000fd5b6000611ae382611587565b9150611aee83611587565b9250828201905080821115611b0657611b05611aa9565b5b92915050565b6000604082019050611b216000830185611a48565b611b2e60208301846116dd565b939250505056fea26469706673582212208dcde66f2e96574f096a62334c174230ffe799d6a84e090f9274b177c17915a264736f6c634300081e0033";

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

    private static String getDeploymentBinary() { return BINARY; }

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
